# OkHttp 的使用方法

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