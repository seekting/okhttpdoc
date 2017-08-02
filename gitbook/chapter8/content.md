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

而client.internalCache()里
```java
 InternalCache internalCache() {
    return cache != null ? cache.internalCache : internalCache;
  }

```
cache则是通过Builder传进去，如果builder里传屯Cache，那么就有缓存的能力。
```java
 builder.cache(new Cache(getCacheDir(), 5 * 1024 * 1024));
```
以上是分配一个5M的缓存的代码，而Cache.java里的实现是通过DiskLruCache实现的。后面一章会说到DiskLruCache

