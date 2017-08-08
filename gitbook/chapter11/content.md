# CallServerInterceptor

CallServerInterceptor实现的是真正发请求，和接收数据的过程


1.writeHeads

2.writeBody

3.readHeader

4.readBody
```java
@Override public Response intercept(Chain chain) throws IOException {
 //...ignore code
    httpCodec.writeRequestHeaders(request);//向服务器发header信息

    Response.Builder responseBuilder = null;
  //...ignore code
        request.body().writeTo(bufferedRequestBody);
        bufferedRequestBody.close();
   //...ignore code

    httpCodec.finishRequest();
    if (responseBuilder == null) {
      responseBuilder = httpCodec.readResponseHeaders(false);
    }

    Response response = responseBuilder
        .request(request)
        .handshake(streamAllocation.connection().handshake())
        .sentRequestAtMillis(sentRequestMillis)
        .receivedResponseAtMillis(System.currentTimeMillis())
        .build();

 //...ignore code
      response = response.newBuilder()
          .body(httpCodec.openResponseBody(response))
          .build();

    //...ignore code


    return response;
  }

```


