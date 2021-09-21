## 使用SerialGC

`java -jar -XX:-UseAdaptiveSizePolicy -XX:+UseSerialGC  elk-service-0.0.1-SNAPSHOT.jar`

```java
Attaching to process ID 26558, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.301-b09

using thread-local object allocation.
Mark Sweep Compact GC

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1073741824 (1024.0MB)
   NewSize                  = 22347776 (21.3125MB)
   MaxNewSize               = 357892096 (341.3125MB)
   OldSize                  = 44761088 (42.6875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 16777216 (16.0MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 4294963200 (4095.99609375MB)
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 20185088 (19.25MB)
   used     = 3682360 (3.5117721557617188MB)
   free     = 16502728 (15.738227844238281MB)
   18.242972237723215% used
Eden Space:
   capacity = 17956864 (17.125MB)
   used     = 2410192 (2.2985382080078125MB)
   free     = 15546672 (14.826461791992188MB)
   13.422120922673358% used
From Space:
   capacity = 2228224 (2.125MB)
   used     = 1272168 (1.2132339477539062MB)
   free     = 956056 (0.9117660522460938MB)
   57.093362247242645% used
To Space:
   capacity = 2228224 (2.125MB)
   used     = 0 (0.0MB)
   free     = 2228224 (2.125MB)
   0.0% used
tenured generation:
   capacity = 44761088 (42.6875MB)
   used     = 10518608 (10.031326293945312MB)
   free     = 34242480 (32.65617370605469MB)
   23.499446662243777% used

12456 interned Strings occupying 896272 bytes.
```



## 使用PS+PO GC


`java -jar -XX:-UseAdaptiveSizePolicy -XX:+UseParallelGC  elk-service-0.0.1-SNAPSHOT.jar`

```
Attaching to process ID 26905, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.301-b09

using thread-local object allocation.
Parallel GC with 1 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1073741824 (1024.0MB)
   NewSize                  = 22282240 (21.25MB)
   MaxNewSize               = 357826560 (341.25MB)
   OldSize                  = 44826624 (42.75MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 16777216 (16.0MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 4294963200 (4095.99609375MB)
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 17039360 (16.25MB)
   used     = 7496552 (7.149269104003906MB)
   free     = 9542808 (9.100730895996094MB)
   43.99550217848558% used
From Space:
   capacity = 2621440 (2.5MB)
   used     = 2609504 (2.488616943359375MB)
   free     = 11936 (0.011383056640625MB)
   99.544677734375% used
To Space:
   capacity = 2621440 (2.5MB)
   used     = 0 (0.0MB)
   free     = 2621440 (2.5MB)
   0.0% used
PS Old Generation
   capacity = 44826624 (42.75MB)
   used     = 12333256 (11.761909484863281MB)
   free     = 32493368 (30.98809051513672MB)
   27.5132385610837% used

12377 interned Strings occupying 889056 bytes.
```

## 使用CMS GC
`java -jar -XX:-UseAdaptiveSizePolicy -XX:+UseConcMarkSweepGC  elk-service-0.0.1-SNAPSHOT.jar`

```
Attaching to process ID 26986, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.301-b09

using parallel threads in the new generation.
using thread-local object allocation.
Concurrent Mark-Sweep GC

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1073741824 (1024.0MB)
   NewSize                  = 22347776 (21.3125MB)
   MaxNewSize               = 67108864 (64.0MB)
   OldSize                  = 44761088 (42.6875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 16777216 (16.0MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 4294963200 (4095.99609375MB)
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 20119552 (19.1875MB)
   used     = 4101304 (3.9113082885742188MB)
   free     = 16018248 (15.276191711425781MB)
   20.38466860494707% used
Eden Space:
   capacity = 17891328 (17.0625MB)
   used     = 1873080 (1.7863082885742188MB)
   free     = 16018248 (15.276191711425781MB)
   10.469206086881869% used
From Space:
   capacity = 2228224 (2.125MB)
   used     = 2228224 (2.125MB)
   free     = 0 (0.0MB)
   100.0% used
To Space:
   capacity = 2228224 (2.125MB)
   used     = 0 (0.0MB)
   free     = 2228224 (2.125MB)
   0.0% used
concurrent mark-sweep generation:
   capacity = 44761088 (42.6875MB)
   used     = 9995960 (9.532890319824219MB)
   free     = 34765128 (33.15460968017578MB)
   22.33180748421486% used

12414 interned Strings occupying 890912 bytes.
```

## 使用G1 GC

```
Attaching to process ID 27046, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.301-b09

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
   used     = 18566128 (17.706039428710938MB)
   free     = 1055175696 (1006.2939605712891MB)
   1.7291054129600525% used
G1 Young Generation:
Eden Space:
   regions  = 3
   capacity = 36700160 (35.0MB)
   used     = 3145728 (3.0MB)
   free     = 33554432 (32.0MB)
   8.571428571428571% used
Survivor Space:
   regions  = 5
   capacity = 5242880 (5.0MB)
   used     = 5242880 (5.0MB)
   free     = 0 (0.0MB)
   100.0% used
G1 Old Generation:
   regions  = 10
   capacity = 25165824 (24.0MB)
   used     = 10177520 (9.706039428710938MB)
   free     = 14988304 (14.293960571289062MB)
   40.44183095296224% used

12427 interned Strings occupying 891120 bytes.
```


结论：
内存占用维度：
目前看G1比较省内存？ 其他CMS/ParellGC/SerialGC 新生代+老年代在在70MB左右，G1GC在59MB左右。(待确定)

吞吐量维度：待压测
响应时间维度： 待压测