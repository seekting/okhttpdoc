# authenticate
authenticate和401错误配合使用，当一次请求，服务端返回401的时候，会走authenticate方法

前题是OkHttpClient设置了Authenticator,支持的代码如下
```java
  OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.authenticator(new Authenticator() {
    public Request authenticate(Route route, Response response){
      Request orignRequest = response.request();
      Request.Builder b = r.newBuilder();
    }
    }
    //...ignore code

```
同样，要是代理服务器的身份认真失败处理:builder.proxyAuthenticator()

而实现的代码在RetryAndFollowUpInterceptor里，当状态码是401或407的时候会走到authenticate方法
```java
private Request followUpRequest(Response userResponse){

        //...ignore code
    int responseCode = userResponse.code();
    switch (responseCode) {
      case HTTP_PROXY_AUTH://407
        return client.proxyAuthenticator().authenticate(route, userResponse);

      case HTTP_UNAUTHORIZED://401
        return client.authenticator().authenticate(route, userResponse);

```

只要返回request不为空，就会接着走请求:
```java
@Override public Response intercept(Chain chain) {
    Request request = chain.request();

    streamAllocation = new StreamAllocation(
        client.connectionPool(), createAddress(request.url()), callStackTrace);


    int followUpCount = 0;
    Response priorResponse = null;
    while (true) {

    //...ignore code
        if (followUp == null) {
            if (!forWebSocket) {
              streamAllocation.release();
            }
            return response;
          }
     //...ignore code
```