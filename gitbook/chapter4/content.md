# OKHttp初探－－Dispatcher

## 首先看成员变量:

```java
private int maxRequests = 64;
private int maxRequestsPerHost = 5;
private @Nullable Runnable idleCallback;

private @Nullable ExecutorService executorService;

private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();

private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();

private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();

```
## 通过成员变量我们能看到它有三个双向队列：
readyAsyncCalls,runningAsyncCalls,runningSyncCalls;

### runningSyncCalls
正在同步请求的队列:

从runningSyncCalls的操作代码来看，正在请求的时候加入到队列里，请求结束之后移除。
有大api会用到：
#### cancelAll　取消所有的请求
#### runningCalls 返回所有正在运行的请求
#### runningCallsCount 返回所有正在请求的数量
> 对于runningSyncCalls用处不是很大，只是为了记录而用的

```java
  synchronized void executed(RealCall call) {
    runningSyncCalls.add(call);
  }
```

```java
 void finished(RealCall call) {
    finished(runningSyncCalls, call, false);
  }

```


### runningAsyncCalls
正在异步请求的队列

与它关联的几个成员有：
#### executorService 线程池
#### maxRequests　正在异步请求的最大数量
#### maxRequestsPerHost 同一个HOST下，最多能容忍的请求数

```java
  synchronized void enqueue(AsyncCall call) {
  //如果正在异步请求的数量超过最大请求数量，或该请求的host的请求数量
  //超过maxRequestsPerHost就把这个请求放进readyAsyncCalls里
    if (runningAsyncCalls.size() < maxRequests
    && runningCallsForHost(call) < maxRequestsPerHost) {
      runningAsyncCalls.add(call);
      executorService().execute(call);
    } else {
      readyAsyncCalls.add(call);
    }
  }

```



### readyAsyncCalls
异步请求准备队列,当正在请求的数量变小，会查看是否要把ready的calls执行
```java
  private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
    int runningCallsCount;
    Runnable idleCallback;
    synchronized (this) {
      if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
      //移除之后，调promoteCalls方法，去尝试将ready的call执行一次
      if (promoteCalls) promoteCalls();
      runningCallsCount = runningCallsCount();
      idleCallback = this.idleCallback;
    }

    //如果正在请求的数量为0，告诉回调者目前处于空闲状态
    if (runningCallsCount == 0 && idleCallback != null) {
      idleCallback.run();
    }
  }

```

promoteCalls 的实现就是把ready的call往runningCall里移，然后调用execute方法
```java
private void promoteCalls() {
    if (runningAsyncCalls.size() >= maxRequests) return; // Already running max capacity.
    if (readyAsyncCalls.isEmpty()) return; // No ready calls to promote.

    for (Iterator<AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
      AsyncCall call = i.next();

      if (runningCallsForHost(call) < maxRequestsPerHost) {
        i.remove();
        runningAsyncCalls.add(call);
        executorService().execute(call);
      }

      if (runningAsyncCalls.size() >= maxRequests) return; // Reached max capacity.
    }
  }

```

以上就是Dispatcher的职责，标记请求中的call，分配线程去处理请求。


