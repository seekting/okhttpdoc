# CacheInterceptor
OkHttp默认是不给缓存的,创建责任连的时候,会把client.internalCache()传进去
```java
Response getResponseWithInterceptorChain() throws IOException {
    //...ignore code
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());
    }
   //...ignore code
    return chain.proceed(originalRequest);
  }

```

而client.internalCache()里是默认是空
```java
 InternalCache internalCache() {
    return cache != null ? cache.internalCache : internalCache;
  }

```
cache则是通过Builder传进去，如果builder里传Cache，那么就有缓存的能力。
```java
 builder.cache(new Cache(getCacheDir(), 5 * 1024 * 1024));
```
以上是分配一个5M的缓存的代码，而Cache.java里的实现是通过DiskLruCache实现的。后面一章会说到DiskLruCache。

OkHttp的缓存策略特点：
1.只支持get请求

2.如果header里有vary会不走缓存

3.POST,PATCH,PUT,DELETE,MOVE不但不会缓存，还会清除掉缓存

Cache.java
```java
@Nullable CacheRequest put(Response response) {
    String requestMethod = response.request().method();

    if (HttpMethod.invalidatesCache(response.request().method())) {
      try {
        remove(response.request());
      } catch (IOException ignored) {
        // The cache cannot be written.
      }
      return null;
    }
    if (!requestMethod.equals("GET")) {
      // Don't cache non-GET responses. We're technically allowed to cache
      // HEAD requests and some POST requests, but the complexity of doing
      // so is high and the benefit is low.
      return null;
    }

    if (HttpHeaders.hasVaryAll(response)) {
      return null;
    }


//...ignore code


  public static boolean invalidatesCache(String method) {
    return method.equals("POST")
        || method.equals("PATCH")
        || method.equals("PUT")
        || method.equals("DELETE")
        || method.equals("MOVE");     // WebDAV
  }
```


