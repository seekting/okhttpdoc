# ConnectInterceptor
ConnectInterceptor的代码不多，主要是把streamAllocation,httpCodec,connection创建好，扔给下一个Interceptor

```java
public Response intercept(Chain chain) throws IOException {
    RealInterceptorChain realChain = (RealInterceptorChain) chain;
    Request request = realChain.request();
    StreamAllocation streamAllocation = realChain.streamAllocation();

    // We need the network to satisfy this request. Possibly for validating a conditional GET.
    boolean doExtensiveHealthChecks = !request.method().equals("GET");
    HttpCodec httpCodec = streamAllocation.newStream(client, doExtensiveHealthChecks);
    RealConnection connection = streamAllocation.connection();

    return realChain.proceed(request, streamAllocation, httpCodec, connection);
  }

```

这里面的Chain就是指RealInterceptorChain，RealInterceptorChain的构造函数里有connection,streamAllocation,httpCodec的初始化，

可为什么ConnectInterceptor又要把新的connection,streamAllocation,httpCodec传进去呢？
```java
 public RealInterceptorChain(List<Interceptor> interceptors, StreamAllocation streamAllocation,
      HttpCodec httpCodec, RealConnection connection, int index, Request request) {
    this.interceptors = interceptors;
    this.connection = connection;
    this.streamAllocation = streamAllocation;
    this.httpCodec = httpCodec;
    this.index = index;
    this.request = request;
  }

```

因为在RealCall.java中，RealInterceptorChain的这几个成员初始化的时候都为空：
```java
Interceptor.Chain chain = new RealInterceptorChain(
        interceptors, null, null, null, 0, originalRequest);

```
如果没有用户自定义的networkInterceptor，那么下一个责任链就是CallServerInterceptor。
也就是connection,streamAllocation,httpCodec最终会给CallServerInterceptor用