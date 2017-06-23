# OkHttp初探－－使用方法及OkHttpClient构造

## 使用
以下是http发请求的代码,OkhttpClient.newCall创建了一个Call对象，
而这个call对象依赖一个Request，最后执行call.execute，得到Response；Response.body()得到String
```java
private void request() {
    OkHttpClient okHttpClient = new OkHttpClient();
    Request.Builder builder = new Request.Builder();
    builder.url("http://www.baidu.com");
    Call call = okHttpClient.newCall(builder.build());
    try {
        Response response = call.execute();
        String str = response.body().string();
        Log.d("seekting", "OkHttpActivity.run()" + str);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

## OkHttpClient构造
它的公开构造,通过new Builder的形式，也就是说：OkHttpClient支持两种构造：
1.new OkHttpClient();
2.Builder.builder();

```java
  public OkHttpClient() {
    this(new Builder());
  }

```

我们可以用第二种方式new出自己想要的Client：
```java
private void newOkHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.readTimeout(2000, TimeUnit.MILLISECONDS);
    builder.writeTimeout(2000,TimeUnit.MILLISECONDS);
    builder.connectTimeout(2000,TimeUnit.MILLISECONDS);
    OkHttpClient okHttpClient = builder.build();
}

```

