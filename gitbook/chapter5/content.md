#OKhttp深入理解--责任链模式处理请求

本章重点讲解OKHttp通过责任链的方式处理请求。不了解责任链的同学可以学习一下责任链设计模式。

首先看一下类图　

![Interceptor类图](Interceptor.png)


![Interceptor时序图](Interceptor-squence.png)
RealCall调用execute会调到getResponseWithInterceptorChain方法
getResponseWithInterceptorChain方法里初始化责链
```java
Response getResponseWithInterceptorChain() throws IOException {
    // Build a full stack of interceptors.
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.addAll(client.interceptors());
    interceptors.add(retryAndFollowUpInterceptor);
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));

    Interceptor.Chain chain = new RealInterceptorChain(
        interceptors, null, null, null, 0, originalRequest);
    return chain.proceed(originalRequest);
  }

```
RealInterceptorChain的proceed方法，创建了新的任务链next(index+1)，让第index个interceptor执行next任务。
假设index=0;
那么会让第0个interceptor执行new nextChain(Chain1),如果interceptor0没有拦截的话，它会调用Chain1的proceed。
Chain1里的index=1;
那么会让第1个interceptor执行new nextChain(Chain2),如果interceptor1没有拦截的话，它会调用Chain2的proceed。

...

责任连就这样一个一个传下去，如果某一个interceptor拦截了，或是到最后一个节点了，责任链停止。

```java
public Response proceed(Request request,
       StreamAllocation streamAllocation, HttpCodec httpCodec,
       RealConnection connection) throws IOException {
   //...ignore code

    RealInterceptorChain next = new RealInterceptorChain(
        interceptors, streamAllocation, httpCodec, connection, index + 1, request);
    Interceptor interceptor = interceptors.get(index);
    Response response = interceptor.intercept(next);
  //...ignore code

    return response;
  }

```



