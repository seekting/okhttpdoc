# WebSocket的支持
okhttp集成了websocket，使用websocket只需要这样写:
```java
  OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("ws://192.168.1.101:8888/ws");
        final WebSocket webSocket = client.newWebSocket(requestBuilder.build(), new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                webSocket.send("hello websocket");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
            }
        });


```
发送消息给服务器
```java
webSocket.send("hello websocket");

```

来看底层实现:
HttpClient.newWebSocket()调用后会new 一个RealWebSocket
```java
 RealWebSocket webSocket = new RealWebSocket(request, listener, new Random());
    webSocket.connect(this);

```

RealWebSocket.java的构造函数:
```java
public RealWebSocket(Request request, WebSocketListener listener, Random random) {
    if (!"GET".equals(request.method())) {//必需要是get请求
      throw new IllegalArgumentException("Request must be GET: " + request.method());
    }
    this.originalRequest = request;
    this.listener = listener;
    this.random = random;

    byte[] nonce = new byte[16];
    random.nextBytes(nonce);
    this.key = ByteString.of(nonce).base64();//生成一个随机的key后面会成为Sec-WebSocket-Key的Header值

    this.writerRunnable = new Runnable() {//起了一个Runnable，无限循环
      @Override public void run() {
        try {
          while (writeOneFrame()) {
          }
        } catch (IOException e) {
          failWebSocket(e, null);
        }
      }
    };
  }

```
然后看它的connect:
1.先是通过源request创建一个新的request，它会加上一些Header信息：Upgrade,Connection,Sec-WebSocket-Key等

2.创建RealCall发第一次请求

3.当第一次请求返回的时候需要走checkResponse

4.如果checkResponse通过，会告诉listener onOpen

5.初始化Reader和Writer initReaderAndWriter

6.把socket的读取时间设成0，表示没有超时时间，也就是长连接

7.启动loopReader
```java
  public void connect(OkHttpClient client) {
      client = client.newBuilder()
          .protocols(ONLY_HTTP1)
          .build();
      final int pingIntervalMillis = client.pingIntervalMillis();
      final Request request = originalRequest.newBuilder()
          .header("Upgrade", "websocket")
          .header("Connection", "Upgrade")
          .header("Sec-WebSocket-Key", key)
          .header("Sec-WebSocket-Version", "13")
          .build();
      call = Internal.instance.newWebSocketCall(client, request);
      call.enqueue(new Callback() {
        @Override public void onResponse(Call call, Response response) {
          try {
            checkResponse(response);
          } catch (ProtocolException e) {
            failWebSocket(e, response);
            closeQuietly(response);
            return;
          }

          // Promote the HTTP streams into web socket streams.
          StreamAllocation streamAllocation = Internal.instance.streamAllocation(call);
          streamAllocation.noNewStreams(); // Prevent connection pooling!
          Streams streams = streamAllocation.connection().newWebSocketStreams(streamAllocation);

          // Process all web socket messages.
          try {
            listener.onOpen(RealWebSocket.this, response);
            String name = "OkHttp WebSocket " + request.url().redact();
            initReaderAndWriter(name, pingIntervalMillis, streams);
            streamAllocation.connection().socket().setSoTimeout(0);
            loopReader();
          } catch (Exception e) {
            failWebSocket(e, null);
          }
        }

        @Override public void onFailure(Call call, IOException e) {
          failWebSocket(e, null);
        }
      });
    }

```

checkResponse:
1.状态码是101表示握手成功，否则就是握手异常

2.Connection的值必需是Upgrade

3.Upgrade的值必需是websocket

4.Sec-WebSocket-Accept的值必需是
ByteString.encodeUtf8(key + WebSocketProtocol.ACCEPT_MAGIC).sha1().base64()
> 也就是说服务器收到客户端的key,再在基础上加上ACCEPT_MAGIC然后sha1+base64得到的字符

```java

void checkResponse(Response response) throws ProtocolException {
    if (response.code() != 101) {
      throw new ProtocolException("Expected HTTP 101 response but was '"
          + response.code() + " " + response.message() + "'");
    }

    String headerConnection = response.header("Connection");
    if (!"Upgrade".equalsIgnoreCase(headerConnection)) {
      throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '"
          + headerConnection + "'");
    }

    String headerUpgrade = response.header("Upgrade");
    if (!"websocket".equalsIgnoreCase(headerUpgrade)) {
      throw new ProtocolException(
          "Expected 'Upgrade' header value 'websocket' but was '" + headerUpgrade + "'");
    }

    String headerAccept = response.header("Sec-WebSocket-Accept");
    String acceptExpected = ByteString.encodeUtf8(key + WebSocketProtocol.ACCEPT_MAGIC)
        .sha1().base64();
    if (!acceptExpected.equals(headerAccept)) {
      throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '"
          + acceptExpected + "' but was '" + headerAccept + "'");
    }
  }
```