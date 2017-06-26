# OKhttp初探－－Call

```java

public interface Call extends Cloneable {
Request request();//返回request对象
Response execute() throws IOException;//执行网络请求，返回Response
void enqueue(Callback responseCallback);//
void cancel();
boolean isExecuted();
boolean isCanceled();
Call clone();

}

```

![call 类图](uml/call.png)