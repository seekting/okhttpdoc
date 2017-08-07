# OKhttp深入理解--RetryAndFollowUpInterceptor

看看RetryAndFollowUpInterceptor的实现:

```java
public Response intercept(Chain chain) throws IOException {

   //...ignore code
    int followUpCount = 0;
    Response priorResponse = null;
    //...开启循环
    while (true) {
     //...ignore code

      Response response = null;
      boolean releaseConnection = true;
      try {
        //执行下一个责任链，如果下一个责任链返回成功，就会跳出循环，否则重试。
        response = ((RealInterceptorChain) chain).proceed(request, streamAllocation, null, null);
        releaseConnection = false;
      } catch (RouteException e) {
        // The attempt to connect via a route failed. The request will not have been sent.
        if (!recover(e.getLastConnectException(), false, request)) {
          throw e.getLastConnectException();
        }
        releaseConnection = false;
        continue;
      } catch (IOException e) {
        // An attempt to communicate with a server failed. The request may have been sent.
        boolean requestSendStarted = !(e instanceof ConnectionShutdownException);
        if (!recover(e, requestSendStarted, request)) throw e;
        releaseConnection = false;
        continue;
      } finally {
        // We're throwing an unchecked exception. Release any resources.
        if (releaseConnection) {
          streamAllocation.streamFailed(null);
          streamAllocation.release();
        }
      }

      // Attach the prior response if it exists. Such responses never have a body.
      if (priorResponse != null) {
        response = response.newBuilder()
            .priorResponse(priorResponse.newBuilder()
                    .body(null)
                    .build())
            .build();
      }

      //判断是否要重定向
      Request followUp = followUpRequest(response);

      if (followUp == null) {
        if (!forWebSocket) {
          streamAllocation.release();
        }
        return response;
      }

      closeQuietly(response.body());

        //重定向的次数不能超过20
      if (++followUpCount > MAX_FOLLOW_UPS) {
        streamAllocation.release();
        throw new ProtocolException("Too many follow-up requests: " + followUpCount);
      }

      if (followUp.body() instanceof UnrepeatableRequestBody) {
        streamAllocation.release();
        throw new HttpRetryException("Cannot retry streamed HTTP body", response.code());
      }

      if (!sameConnection(response, followUp.url())) {
        streamAllocation.release();
        streamAllocation = new StreamAllocation(
            client.connectionPool(), createAddress(followUp.url()), callStackTrace);
      } else if (streamAllocation.codec() != null) {
        throw new IllegalStateException("Closing the body of " + response
            + " didn't close its backing stream. Bad interceptor?");
      }

      request = followUp;
      priorResponse = response;
    }
  }

```

1.在执行下一个责任链的时候，如果有异常，它会调用recover来判断是否需要重新请求

2.如果followUp重定向成功，会重新创建Request去发请求

3.超时重试需要自己实现，OKHttp超时重试没做支持

4.身份认证失败的话可以走重新认证身份的逻辑

重点看followUpRequest

```java
 private Request followUpRequest(Response userResponse){
    //...ignore code
    int responseCode = userResponse.code();
    final String method = userResponse.request().method();
    switch (responseCode) {
      case HTTP_PROXY_AUTH://407 代理服务器身份证异常
        //...ignore code
        return client.proxyAuthenticator().authenticate(route, userResponse);

      case HTTP_UNAUTHORIZED://401 身份证异常
        return client.authenticator().authenticate(route, userResponse);

      case HTTP_PERM_REDIRECT://308
      case HTTP_TEMP_REDIRECT://307
        if (!method.equals("GET") && !method.equals("HEAD")) {
          return null;
        }
    //...ignore code
  }

```