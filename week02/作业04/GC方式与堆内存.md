
## wrk 压测gateway-server.jar

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
