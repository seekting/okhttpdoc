# Okhttp初探之Request
Request也是通过Builder模式构建的，可见Square比较喜欢用builder

以下是Request的所有成员变量
```java
final HttpUrl url;
final String method;
final Headers headers;
final @Nullable RequestBody body;
final Object tag;

private volatile CacheControl cacheControl; // Lazily initialized.


```
## HttpUrl
构造的的方式也是通过Builder，它的属性有:

```java
@Nullable String scheme;
String encodedUsername = "";
String encodedPassword = "";
@Nullable String host;
int port = -1;
final List<String> encodedPathSegments = new ArrayList<>();
@Nullable List<String> encodedQueryNamesAndValues;
@Nullable String encodedFragment;

```

HttpUrl里有解析url的逻辑，总之能解析出host,path,param等等

## method
method就是http协议里的的请求方式:get,post,put,delete,patch,head等

## headers

headers就是请求头部，http协议里的。key:value形式;okhttp对Header又做了一层封装
最终是以String数组保存着
```java
public final class Headers {
  private final String[] namesAndValues;


```

## RequestBody

如果是post请求，是有body的。而get不需要，get的参数是在url里配的

```java
public Builder method(String method, @Nullable RequestBody body) {
      if (method == null) throw new NullPointerException("method == null");
      if (method.length() == 0) throw new IllegalArgumentException("method.length() == 0");
      if (body != null && !HttpMethod.permitsRequestBody(method)) {
        throw new IllegalArgumentException("method " + method + " must not have a request body.");
      }
      if (body == null && HttpMethod.requiresRequestBody(method)) {
        throw new IllegalArgumentException("method " + method + " must have a request body.");
      }
      this.method = method;
      this.body = body;
      return this;
    }


```

```java
//get传body是空
    public Builder get() {
      return method("GET", null);
    }

```

## tag

是request里预留的一个成员，有点像view的tag

##　newBuilder
发现它们只要有builder的模式的地方都会有个newBuilder方法
```java
public Builder newBuilder() {
    return new Builder(this);
  }

```

也就是通过builder能创建出Request实体，通过实体也能创建出一个Builder，一种可逆的实现形式



