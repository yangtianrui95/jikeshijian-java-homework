从线上找了台机器分析下JVM各维度的运行情况，看能否所有优化。

## 内存维度


使用jmap查看堆内存使用情况。

`jmap -heap <pid>`

```
Attaching to process ID 6108, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.65-b01

using parallel threads in the new generation.
using thread-local object allocation.

Concurrent Mark-Sweep GC

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 536870912 (512.0MB)
   NewSize                  = 174456832 (166.375MB)
   MaxNewSize               = 174456832 (166.375MB)
   OldSize                  = 362414080 (345.625MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 268435456 (256.0MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 157024256 (149.75MB)
   used     = 6034200 (5.754661560058594MB)
   free     = 150990056 (143.9953384399414MB)
   3.842845783010747% used
Eden Space:
   capacity = 139591680 (133.125MB)
   used     = 4675480 (4.458885192871094MB)
   free     = 134916200 (128.6661148071289MB)
   3.3493973279782865% used
From Space:
   capacity = 17432576 (16.625MB)
   used     = 1358720 (1.2957763671875MB)
   free     = 16073856 (15.3292236328125MB)
   7.794143562030075% used
To Space:
   capacity = 17432576 (16.625MB)
   used     = 0 (0.0MB)
   free     = 17432576 (16.625MB)
   0.0% used
concurrent mark-sweep generation:
   capacity = 362414080 (345.625MB)
   used     = 171931176 (163.96634674072266MB)
   free     = 190482904 (181.65865325927734MB)
   47.44053431919643% used

64006 interned Strings occupying 8098024 bytes.
```

从上面的信息可以看出，GC方式为CMS+ParNew，同时支持TLAB，堆配置方面，Xmx为512M，其中新生代为166MB，老年代为346MB。比例大概为3:7。

非堆内存方面，存储常量的MetaspaceSize占256.0MB，CompressedClassSpaceSize 压缩指针最多占1GB左右。

继续使用`ps aux`查看进程内存占用：


USER | PID | %CPU | %MEM | VSZ(KB) | RSS(KB) | TTY | STAT | START | TIME | COMMAND
--- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ---
root | 6108 | 0.9 | 12 | 3484536 | 967940 | ? | Sl |  8月22 | 401:04 |        java

可以看出实际占用内存大约在1GB左右（一般我们看RSS占用）。

### G1GC Heap分析
上面是CMS和ParNewGC的堆情况，下面我们分析下G1 GC的堆内存情况。

添加VM参数`-XX:+UseG1GC`启动G1GC。
再使用jmap查看，情况如下
```
using thread-local object allocation.
Garbage-First (G1) GC with 1 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1073741824 (1024.0MB)
   NewSize                  = 1048576 (1.0MB)
   MaxNewSize               = 643825664 (614.0MB)
   OldSize                  = 4194304 (4.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 16777216 (16.0MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 4294963200 (4095.99609375MB)
   G1HeapRegionSize         = 1048576 (1.0MB)

Heap Usage:
G1 Heap:
   regions  = 1024
   capacity = 1073741824 (1024.0MB)
   used     = 39620600 (37.78514862060547MB)
   free     = 1034121224 (986.2148513793945MB)
   3.689955919981003% used
G1 Young Generation:
Eden Space:
   regions  = 25
   capacity = 39845888 (38.0MB)
   used     = 26214400 (25.0MB)
   free     = 13631488 (13.0MB)
   65.78947368421052% used
Survivor Space:
   regions  = 2
   capacity = 2097152 (2.0MB)
   used     = 2097152 (2.0MB)
   free     = 0 (0.0MB)
   100.0% used
G1 Old Generation:
   regions  = 12
   capacity = 25165824 (24.0MB)
   used     = 11309048 (10.785148620605469MB)
   free     = 13856776 (13.214851379394531MB)
   44.93811925252279% used

12846 interned Strings occupying 929360 bytes.
```

G1GC采用逻辑分代的概念，即新生代和老年代只在逻辑上存在，在物理内存上是连续的，并没有进行区分。

可以看到的信息有RegionSize为1MB默认，整个Heap共有1024个Region，最大支持1G堆内存。

Eden区已经创建25个Region，Old区已经创建12个Region。使用率分别为65%和44%。







## GC维度

jstat查看gc情况

`jstat -gc <pid>`

S0C  |  S1C  |  S0U  |  S1U  |    EC   |    EU   |     OC   |      OU   |    MC   |  MU  |  CCSC |  CCSU  | YGC  |   YGCT  |  FGC |   FGCT  |   GCT
 --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- 
17024.0 | 17024.0 | 1326.9 |  0.0 |  136320.0 | 39525.0 |  353920.0 |  167901.5 | 155520.0 | 145749.4 | 19328.0 | 17633.6 |  12458 |  152.452 |  6   |   0.573 | 153.025


jinfo -gc 各详细参数说明如下：

- S0C ：第一个Survior的大小，S0 Capacity
- S1C：第二个Survior的大小
- S0U：第一个Survior已经使用的大小
- S1U：第二个Survior已经使用的大小
- EC : eden区的大小
- EU: eden区已经使用的大小
- OC：老年代的大小
- OU：老年代使用的大小
- MC：元空间大小
- MU：元空间已经使用的大小
- CCSC：压缩类空间大小
- CCSU：压缩类空间使用大小
- YGC：YGC次数
- TGCT：YGC垃圾回收消耗的时间
- FGC：Full GC回收次数
- FGCT：Ful GC回收消耗的时间
- GCT：GC总耗时

以上存储单位为kb，时间单位为s。

从上面信息看出，Survior大约使用了1MB，Eden使用了大约39MB，Old Gen使用了大约167MB，Metaspace 使用了大约145MB。

GC维度上，FullGC触发6次，总耗时0.573s，YGC触发12458次，耗时152s。


## 线程维度

`jstack -l` 查看当前线程运行状态。

```java
"OkHttp ConnectionPool" #22461 daemon prio=5 os_prio=0 tid=0x00007f000066a000 nid=0x1a6 in Object.wait() [0x00007effc8601000]
   java.lang.Thread.State: TIMED_WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	at java.lang.Object.wait(Object.java:460)
	at com.squareup.okhttp.ConnectionPool$1.run(ConnectionPool.java:101)
	- locked <0x00000000f07b2d58> (a com.squareup.okhttp.ConnectionPool)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)

   Locked ownable synchronizers:
	- <0x00000000e6c0b998> (a java.util.concurrent.ThreadPoolExecutor$Worker)

"Attach Listener" #18208 daemon prio=9 os_prio=0 tid=0x00007efff40f6800 nid=0x6d8a waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

   Locked ownable synchronizers:
	- None

....
```

可以用于查看是否存在死锁问题。


## VM配置维度


`jinfo pid` 查看配置

```
Debugger attached successfully.
Server compiler detected.
JVM version is 25.65-b01
Java System Properties:

java.runtime.name = Java(TM) SE Runtime Environment
java.vm.version = 25.65-b01
sun.boot.library.path = /usr/java/jdk1.8.0_65/jre/lib/amd64
java.vendor.url = http://java.oracle.com/
java.vm.vendor = Oracle Corporation
path.separator = :
file.encoding.pkg = sun.io
java.vm.name = Java HotSpot(TM) 64-Bit Server VM
sun.os.patch.level = unknown
sun.java.launcher = SUN_STANDARD
user.country = US
user.dir = /data/shunlu_dubbo/server-service
java.vm.specification.name = Java Virtual Machine Specification
PID = 6108
java.runtime.version = 1.8.0_65-b17
java.awt.graphicsenv = sun.awt.X11GraphicsEnvironment
skywalking.collector.backend_service = 127.0.0.1:11800
dubbo.application.logger = slf4j
os.arch = amd64
java.endorsed.dirs = /usr/java/jdk1.8.0_65/jre/lib/endorsed
line.separator =

java.io.tmpdir = /tmp
java.vm.specification.vendor = Oracle Corporation
os.name = Linux
sun.jnu.encoding = UTF-8
java.library.path = /usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
spring.beaninfo.ignore = true
sun.nio.ch.bugLevel =
java.specification.name = Java Platform API Specification
java.class.version = 52.0
java.net.preferIPv4Stack = true
sun.management.compiler = HotSpot 64-Bit Tiered Compilers
os.version = 3.10.0-957.5.1.el7.x86_64
user.home = /root
user.timezone = Asia/Shanghai
catalina.useNaming = false
java.awt.printerjob = sun.print.PSPrinterJob
file.encoding = UTF-8
java.specification.version = 1.8
catalina.home = /tmp/tomcat.5093527478169480461.9988
user.name = root
java.vm.specification.version = 1.8
sun.arch.data.model = 64
sun.java.command = com.shunlu.server.ServerServiceApplication
java.home = /usr/java/jdk1.8.0_65/jre
user.language = en
java.specification.vendor = Oracle Corporation
awt.toolkit = sun.awt.X11.XToolkit
java.vm.info = mixed mode
java.version = 1.8.0_65
java.ext.dirs = /usr/java/jdk1.8.0_65/jre/lib/ext:/usr/java/packages/lib/ext
sun.boot.class.path = /usr/java/jdk1.8.0_65/jre/lib/resources.jar:/usr/java/jdk1.8.0_65/jre/lib/rt.jar:/usr/java/jdk1.8.0_65/jre/lib/sunrsasign.jar:/usr/java/jdk1.8.0_65/jre/lib/jsse.jar:/usr/java/jdk1.8.0_65/jre/lib/jce.jar:/usr/java/jdk1.8.0_65/jre/lib/charsets.jar:/usr/java/jdk1.8.0_65/jre/lib/jfr.jar:/usr/java/jdk1.8.0_65/jre/classes
skywalking.agent.service_name = server-http
java.awt.headless = true
java.vendor = Oracle Corporation
catalina.base = /tmp/tomcat.5093527478169480461.9988
file.separator = /
java.vendor.url.bug = http://bugreport.sun.com/bugreport/
sun.io.unicode.encoding = UnicodeLittle
sun.font.fontmanager = sun.awt.X11FontManager
sun.cpu.endian = little
sun.cpu.isalist =

VM Flags:
Non-default VM flags: 
-XX:CICompilerCount=2 
-XX:CMSInitiatingOccupancyFraction=70 
-XX:+CMSParallelRemarkEnabled 
-XX:+DisableExplicitGC 
-XX:InitialHeapSize=536870912 
-XX:LargePageSizeInBytes=134217728 
-XX:MaxHeapSize=536870912 
-XX:MaxNewSize=174456832 
-XX:MaxTenuringThreshold=6 
-XX:MetaspaceSize=268435456 
-XX:MinHeapDeltaBytes=196608 
-XX:NewSize=174456832 
-XX:OldPLABSize=16 
-XX:OldSize=362414080 
-XX:ThreadStackSize=256 
-XX:+UseCMSCompactAtFullCollection 
-XX:+UseCMSInitiatingOccupancyOnly 
-XX:+UseCompressedClassPointers 
-XX:+UseCompressedOops 
-XX:+UseConcMarkSweepGC 
-XX:-UseFastAccessorMethods 
-XX:+UseParNewGC
Command line:  
-javaagent:/data/soft/skywalking/apache-skywalking-apm-bin/agent/skywalking-agent.jar 
-Dskywalking.agent.service_name=server-http 
-Dskywalking.collector.backend_service=127.0.0.1:11800 
-javaagent:/work/expotor/jmx_prometheus_javaagent-0.9.jar=3011:/work/expotor/simple-config.yml 
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=7777 
-Djava.awt.headless=true 
-Djava.net.preferIPv4Stack=true 
-Ddubbo.application.logger=slf4j 
-Xmx512m 
-Xms512m 
-XX:MetaspaceSize=256m 
-Xss256k 
-XX:+DisableExplicitGC 
-XX:+UseConcMarkSweepGC 
-XX:+CMSParallelRemarkEnabled 
-XX:+UseCMSCompactAtFullCollection 
-XX:LargePageSizeInBytes=128m 
-XX:+UseFastAccessorMethods 
-XX:+UseCMSInitiatingOccupancyOnly 
-XX:CMSInitiatingOccupancyFraction=70

```