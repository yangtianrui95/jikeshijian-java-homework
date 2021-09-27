## GC 日志

## SerialGC 日志分析

```
[GC (Allocation Failure) [DefNew: 15162K->1242K(15360K), 0.0058139 secs] 19841K->6895K(49536K), 0.0058382 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
```

根据上面的YGC日志，有几个关键信息要看出：

1. 其中 Allocation Failure表示触发GC原因为空间不足。
2. DefNew表示SerialGC的新生代，这次GC 新生代空间从15162k下降至1242k，耗时5ms。
3. 整个堆空间从19841k下降至6895k，耗时5ms。


```
[Full GC (Metadata GC Threshold) [Tenured: 5653K->6527K(34176K), 0.0217220 secs] 7483K->6527K(49536K), [Metaspace: 15849K->15849K(16688K)], 0.0217555 secs] [Times: user=0.06 sys=0.00, real=0.02 secs]
```
上面是一条SerialGC的Full GC日志
信息如下：
1. Tenured老年代从5653K上升到6527K，总共老年代空间为34176k。
2. 整个堆空间从7483k下降至6537k。
3. Metaspace 区没有变化。

## ParallelGC 日志格式分析

YGC
```
[GC (Allocation Failure) [PSYoungGen: 13196K->384K(14592K)] 31101K->18572K(38912K), 0.0043607 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
```

FullGC
```
[Full GC (Ergonomics) [PSYoungGen: 2290K->0K(14592K)] [ParOldGen: 23196K->19969K(34304K)] 25486K->19969K(48896K), [Metaspace: 23359K->23321K(24880K)], 0.0670752 secs] [Times: user=0.19 sys=0.01, real=0.07 secs]
```

ParallelGC和SerialGC格式类似，只不过新生代/老年代名称和SerialGC不同而已。


## CMS GC日志格式

CMS在新生代使用的是ParNewGC，所以YGC日志与SerialGC和ParalellGC基本相同

```
[GC (Allocation Failure) [ParNew: 15103K->1333K(15360K), 0.0035725 secs] 20866K->7836K(49536K), 0.0035963 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
```

当触发FullGC时，此时的日志会与其他日志有很大不同
可以看到如下信息：

```
[GC (CMS Initial Mark) [1 CMS-initial-mark: 6503K(34176K)] 8234K(49536K), 0.0013258 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[CMS-concurrent-mark-start]
[CMS-concurrent-mark: 0.031/0.031 secs] [Times: user=0.12 sys=0.01, real=0.03 secs]
[CMS-concurrent-preclean-start]
[CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
[CMS-concurrent-abortable-preclean-start]
[GC (Allocation Failure) [ParNew: 15029K->1157K(15360K), 0.0051178 secs] 21532K->8243K(49536K), 0.0051430 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
[CMS-concurrent-abortable-preclean: 0.021/0.108 secs] [Times: user=0.35 sys=0.01, real=0.10 secs]
[GC (CMS Final Remark) [YG occupancy: 8358 K (15360 K)][Rescan (parallel) , 0.0019976 secs][weak refs processing, 0.0000316 secs][class unloading, 0.0026277 secs][scrub symbol table, 0.0020630 secs][scrub string table, 0.0000961 secs][1 CMS-remark: 7086K(34176K)] 15445K(49536K), 0.0073523 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
[CMS-concurrent-sweep-start]
[CMS-concurrent-sweep: 0.004/0.004 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
[CMS-concurrent-reset-start]
[CMS-concurrent-reset: 0.007/0.007 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
```

## wrk 压测gateway-server.jar

### 汇总
压测环境：CentOS7 4核 8G内存虚拟机


| GC方式| 启动命令 |P90延时 | QPS |
|----|----|----|----|
|G1GC|java -jar -Xmx50m  -XX:+UseG1GC  -XX:+PrintGC  ~/gateway-server-0.0.1-SNAPSHOT.jar |38ms|5666|
|SerialGC|java -jar -Xmx50m  -XX:+UseSerialGC  -XX:+PrintGC  ~/gateway-server-0.0.1-SNAPSHOT.jar |28ms|5766|
|ParallelGC|java -jar -Xmx50m  -XX:+UseParallelGC  -XX:+PrintGC  ~/gateway-server-0.0.1-SNAPSHOT.jar | 48ms|4801|
|CMSGC|java -jar -Xmx50m  -XX:+UseConcMarkSweepGC  -XX:+PrintGC  ~/gateway-server-0.0.1-SNAPSHOT.jar | 25ms| 6743|

### G1GC

https://gceasy.io/my-gc-report.jsp?p=c2hhcmVkLzIwMjEvMDkvMjYvLS1nMWdjLmxvZy0tMTQtNTQtMjk=&channel=WEB

```
Running 30s test @ http://192.168.1.53:8088/api/hello
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    24.52ms   36.39ms 662.64ms   93.87%
    Req/Sec     1.44k   484.62     2.88k    65.46%
  Latency Distribution
     50%   14.73ms
     75%   21.90ms
     90%   38.98ms
     99%  214.12ms
  170057 requests in 30.01s, 20.30MB read
  Socket errors: connect 0, read 0, write 0, timeout 98
Requests/sec:   5666.07
Transfer/sec:    692.68KB
```



### SERIALGC
https://gceasy.io/my-gc-report.jsp?p=c2hhcmVkLzIwMjEvMDkvMjYvLS1zZXJpYWxnYy5sb2ctLTE0LTU2LTU4&channel=WEB

```java
Running 30s test @ http://192.168.1.53:8088/api/hello
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    22.81ms   36.96ms 740.90ms   95.46%
    Req/Sec     1.46k   338.41     2.41k    73.91%
  Latency Distribution
     50%   15.45ms
     75%   20.21ms
     90%   28.93ms
     99%  188.39ms
  173229 requests in 30.04s, 20.68MB read
  Socket errors: connect 0, read 0, write 0, timeout 100
Requests/sec:   5766.45
Transfer/sec:    704.95KB
```



### PARALLELGC

https://gceasy.io/my-gc-report.jsp?p=c2hhcmVkLzIwMjEvMDkvMjYvLS1wc2djLmxvZy0tMTUtMC0zMQ==&channel=WEB

```
Running 30s test @ http://192.168.1.53:8088/api/hello
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    28.41ms   38.32ms 730.06ms   92.62%
    Req/Sec     1.22k   376.01     2.34k    69.93%
  Latency Distribution
     50%   17.77ms
     75%   26.08ms
     90%   48.47ms
     99%  216.07ms
  144279 requests in 30.05s, 17.22MB read
  Socket errors: connect 0, read 0, write 0, timeout 66
Requests/sec:   4801.15
Transfer/sec:    586.94KB
```



### CMSGC

https://gceasy.io/my-gc-report.jsp?p=c2hhcmVkLzIwMjEvMDkvMjYvLS1jbXNnYy5sb2ctLTE1LTQtMjY=&channel=WEB

```
Running 30s test @ http://192.168.1.53:8088/api/hello
4 threads and 100 connections
Thread Stats   Avg      Stdev     Max   +/- Stdev
Latency    19.44ms   29.07ms 678.54ms   95.21%
Req/Sec     1.71k   387.12     2.42k    71.60%
Latency Distribution
50%   13.13ms
75%   17.14ms
90%   25.85ms
99%  173.43ms
202691 requests in 30.06s, 24.20MB read
Socket errors: connect 0, read 0, write 0, timeout 99
Requests/sec:   6743.19
Transfer/sec:    824.36KB
```
