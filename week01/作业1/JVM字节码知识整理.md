[TOC]

## 字节码

> Java Bytecode 由单字节指令组成，理论上最多支持256个操作码（opcode）。实际上Java只使用了200左右个操作码，还有一些操作码留给调试操作。

操作码主要分为四个大类：
- 栈操作指令，包含与局部变量交互的指令
- 程序流程控制指令
- 对象操作指令，方法调用的指令
- 算术运算以及类型转换指令


### 字节码查看方式

#### 简单概览

先编写一个Java类，代码如下

```java

public class ByteCodeTest {
    public static void main(String[] args) {
        final Main.User user = new Main.User();
    }
}
```

然后使用javac 编译

`javac /Users/yangtianrui/jvm_test/src/main/java/mm/ByteCodeTest.java `

使用javap -c 查看字节码指令

```java
Compiled from "ByteCodeTest.java"
public class mm.ByteCodeTest {
  public mm.ByteCodeTest();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: new           #2                  // class mm/Main$User
       3: dup
       4: invokespecial #3                  // Method mm/Main$User."<init>":()V
       7: astore_1
       8: return
}
```

aload_0 将本地变量表中的第0个变量加载到栈上，对于非静态函数来说，这个变量是this。

我们分析下main方法中的字节码

`new #2`
new 表示操作符，#2为操作数，代表常量池中的地址
javap将#2指向的内容也输出出来了，这里是`class mm/Main$User`

然后通过`invokespecial` 调用到User类的构造器中。

invokespecial #3 ( #3为操作数，表示常量池中的地址)

通过astore_1将变量存储在本地变量表。

注意：使用javap -c -verbose可以将常量池中的数据进行输出

也就是说，会多出如下内容。

```java

Classfile /Users/yangtianrui/jvm_test/src/main/java/mm/ByteCodeTest.class
  Last modified 2021-9-21; size 358 bytes
  MD5 checksum f34b9f481d2f42be8352c98a1bc858a9
  Compiled from "ByteCodeTest.java"
public class mm.ByteCodeTest
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #5.#14         // java/lang/Object."<init>":()V
   #2 = Class              #16            // mm/Main$User
   #3 = Methodref          #2.#14         // mm/Main$User."<init>":()V
   #4 = Class              #19            // mm/ByteCodeTest
   #5 = Class              #20            // java/lang/Object
   #6 = Utf8               <init>
   #7 = Utf8               ()V
   #8 = Utf8               Code
   #9 = Utf8               LineNumberTable
  #10 = Utf8               main
  #11 = Utf8               ([Ljava/lang/String;)V
  #12 = Utf8               SourceFile
  #13 = Utf8               ByteCodeTest.java
  #14 = NameAndType        #6:#7          // "<init>":()V
  #15 = Class              #21            // mm/Main
  #16 = Utf8               mm/Main$User
  #17 = Utf8               User
  #18 = Utf8               InnerClasses
  #19 = Utf8               mm/ByteCodeTest
  #20 = Utf8               java/lang/Object
  #21 = Utf8               mm/Main
```


#### 循环控制指令分析


下面我们分析下循环控制语句的JVM指令。

java代码如下:
```java
public class ByteCodeTest {
    public static void main(String[] args) {
        int a = 0;
        for (int i = 0; i < 100; i++) {
            a += i;
        }
    }
}
```

先使用javac编译后再使用javap -c 查看编译后的字节码

```java

Classfile /Users/yangtianrui/jvm_test/src/main/java/mm/ByteCodeTest.class
  Last modified 2021-9-21; size 340 bytes
  MD5 checksum f1f8d4bb3eb0bcdee78b43006d4232c9
  Compiled from "ByteCodeTest.java"
public class mm.ByteCodeTest
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #3.#13         // java/lang/Object."<init>":()V
   #2 = Class              #14            // mm/ByteCodeTest
   #3 = Class              #15            // java/lang/Object
   #4 = Utf8               <init>
   #5 = Utf8               ()V
   #6 = Utf8               Code
   #7 = Utf8               LineNumberTable
   #8 = Utf8               main
   #9 = Utf8               ([Ljava/lang/String;)V
  #10 = Utf8               StackMapTable
  #11 = Utf8               SourceFile
  #12 = Utf8               ByteCodeTest.java
  #13 = NameAndType        #4:#5          // "<init>":()V
  #14 = Utf8               mm/ByteCodeTest
  #15 = Utf8               java/lang/Object
{
  public mm.ByteCodeTest();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=3, args_size=1
         0: iconst_0
         1: istore_1
         2: iconst_0
         3: istore_2
         4: iload_2
         5: bipush        100
         7: if_icmpge     20
        10: iload_1
        11: iload_2
        12: iadd
        13: istore_1
        14: iinc          2, 1
        17: goto          4
        20: return
      LineNumberTable:
        line 5: 0
        line 6: 2
        line 7: 10
        line 6: 14
        line 9: 20
      StackMapTable: number_of_entries = 2
        frame_type = 253 /* append */
          offset_delta = 4
          locals = [ int, int ]
        frame_type = 250 /* chop */
          offset_delta = 15
}
SourceFile: "ByteCodeTest.java"
```


Class文件头部分我们暂时不去理会，主要关注操作指令部分。

指令部分的格式如下：
偏移量：操作符 操作数
比如`5: bipush        100`


其中

0: iconst_0 表示将常量0压入操作数栈
1: istore_1 表示弹出栈顶元素，并存放在本地变量表的第1个位置，此时对应我们代码中的a变量
2: iconst_0 继续将常量0入栈
3: istore_2 弹出栈顶元素，并将数值保存在局部变量表的第二个位置。此时对应我们代码的i变量。
4: iload_2 第二个变量入栈
5: bipush        100 将100压入操作数栈

这里需要注意下bipush和iconst的区别：
- 如果要入栈的常量在-1~5，那么使用iconst指令进行入栈
- 如果要入栈的常量在 -128~127，那么使用bipush 指令入栈
- 其他入栈指令，取值 -32768~32767 采用 sipush指令，取值 -2147483648~2147483647 采用 ldc 指令。


7: if_icmpge     20 如果刚才压栈的变量大于100（刚才入栈的变量）的话，跳转到第20行的位置，也就是return语句
10: iload_1 从本地变量表中入栈第一个变量
11: iload_2 从本地变量表中入栈第二个变量
12: iadd 两个变量执行int类型的加法
13: istore_1 变量一存入本地变量表
14: iinc          2, 1  把常量1加到变量二上
17: goto 跳转到偏移量4
20: return 函数跳出


我们还需要注意一点就是偏移量问题。下面用一个图描述java代码与字节码之间的映射关系。
![image](https://kswapd.cn/wp-content/uploads/2021/09/jvm_01.png)


#### 简单加法与条件判断

java代码如下

```
public class ByteCodeTest {
    public static void main(String[] args) {
        double a = Math.random() + 0.1;
        if (a < .5d) {
            return;
        }
        System.out.println("More than 0.5");
    }
}
```

编译后字节码如下

```java
Classfile /Users/yangtianrui/jvm_test/src/main/java/mm/ByteCodeTest.class
  Last modified 2021-9-21; size 554 bytes
  MD5 checksum 23d02b64ca48c89c37714ef647e722db
  Compiled from "ByteCodeTest.java"
public class mm.ByteCodeTest
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
{
  public mm.ByteCodeTest();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=4, locals=3, args_size=1
         0: invokestatic  #2                  // Method java/lang/Math.random:()D
         3: ldc2_w        #3                  // double 0.1d
         6: dadd
         7: dstore_1
         8: dload_1
         9: ldc2_w        #5                  // double 0.5d
        12: dcmpg
        13: ifge          17
        16: return
        17: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
        20: ldc           #8                  // String More than 0.5
        22: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        25: return
      LineNumberTable:
        line 5: 0
        line 6: 8
        line 7: 16
        line 9: 17
        line 10: 25
      StackMapTable: number_of_entries = 1
        frame_type = 252 /* append */
          offset_delta = 17
          locals = [ double ]
}
SourceFile: "ByteCodeTest.java"
```

下面我们来逐渐分析下指令：

0: invokestatic  #2  通过invokestatic调用Math.random，并将返回值压入栈
3: ldc2_w        #3  将浮点数0.1压入栈
6: dadd  执行两个浮点数的相加操作
7: dstore_1 将变量1存入本地变量表
8: dload_1 将变量1入栈
9: ldc2_w        #5  将浮点数0.5入栈
12: dcmpg        比较第一个变量是否比0.5大，然后将结果 [-1   0  1] 压入栈顶
13: ifge          17   当栈顶大于等于0时，跳转到偏移量17的位置
17: getstatic     #7  从类中获取静态字段PrintStream
20: ldc           #8  字符串常量(More than 0.5)入栈
22: invokevirtual #9  调用PrintStream.println方法
25: return 返回


#### 异常处理指令

##### athrow抛出异常指令

java中抛出异常是以athorw指令进行的，


仍然先以一个简单的代码为例，如果随机数大于0.5以上则抛出异常。

```java
public class ByteCodeTest {
    public static void main(String[] args) {
        double a = Math.random();
        if (a < .5d) {
            throw new RuntimeException("异常");
        }
    }
}
```

反编译后的字节码如下：

```java
Classfile /Users/yangtianrui/jvm_test/src/main/java/mm/ByteCodeTest.class
  Last modified 2021-9-21; size 502 bytes
  MD5 checksum c66c3fca7a1c9bec726e60a078d9d702
public class mm.ByteCodeTest
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
{
  public mm.ByteCodeTest();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lmm/ByteCodeTest;

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=4, locals=3, args_size=1
         0: invokestatic  #2                  // Method java/lang/Math.random:()D
         3: dstore_1
         4: dload_1
         5: ldc2_w        #3                  // double 0.5d
         8: dcmpg
         9: ifge          22
        12: new           #5                  // class java/lang/RuntimeException
        15: dup
        16: ldc           #6                  // String 异常
        18: invokespecial #7                  // Method java/lang/RuntimeException."<init>":(Ljava/lang/String;)V
        21: athrow
        22: return
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      23     0  args   [Ljava/lang/String;
            4      19     1     a   D
      StackMapTable: number_of_entries = 1
        frame_type = 252 /* append */
          offset_delta = 22
          locals = [ double ]
}
```

可以看到最后是使用athrow进行异常的抛出，athrow的执行过程为先从栈帧开始，逐渐去找对应的异常处理表。

这个示例代码我们没有使用try-catch处理异常，所以不会找到对应的ExceptionTable，异常会直接中断程序。

我们将代码改为

```java
public static void main(String[] args) {
    try {
        double a = Math.random();
        if (a < .5d) {
            throw new RuntimeException("异常");
        }
    } catch (Exception e) {
        System.out.println("Catch");
    } finally {
        System.out.println("Finally");
    }
}
```


继续反编译，可以看出多了ExceptionTable字段

```java
public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=4, locals=4, args_size=1
      // 注释：try 块开始
         0: invokestatic  #2                  // Method java/lang/Math.random:()D
         3: dstore_1
         4: dload_1
         5: ldc2_w        #3                  // double 0.5d
         8: dcmpg
         9: ifge          22
        12: new           #5                  // class java/lang/RuntimeException
        15: dup
        16: ldc           #6                  // String 异常
        18: invokespecial #7                  // Method java/lang/RuntimeException."<init>":(Ljava/lang/String;)V
        21: athrow
      // 注释：try 代码块结束
      // 注释：finally (1) 代码块开始 ：不重新抛出异常
        22: getstatic     #8                  // Field java/lang/System.out:Ljava/io/PrintStream;
        25: ldc           #9                  // String Finally
        27: invokevirtual #10                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        30: goto          64
      // 注释：finally (1) 代码块结束 ：不重新抛出异常
      // 注释：catch - finally 代码块开始
        33: astore_1
        34: getstatic     #8                  // Field java/lang/System.out:Ljava/io/PrintStream;
        37: ldc           #12                 // String Catch
        39: invokevirtual #10                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        42: getstatic     #8                  // Field java/lang/System.out:Ljava/io/PrintStream;
        45: ldc           #9                  // String Finally
        47: invokevirtual #10                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        50: goto          64
      // 注释：catch - finally 代码块结束
      // 注释：finally (2) 代码块开始 ：重新抛出异常
        53: astore_3
        54: getstatic     #8                  // Field java/lang/System.out:Ljava/io/PrintStream;
        57: ldc           #9                  // String Finally
        59: invokevirtual #10                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        62: aload_3
        63: athrow
      // 注释：finally (2) 代码块结束 ：重新抛出异常
        64: return
      Exception table:
         from    to  target type
             0    22    33   Class java/lang/Exception
             0    22    53   any
            33    42    53   any
```

加入try-catch后字节码会新增一处Exceptio table信息，我们可以看到里面以后有三条记录，对应了三处可能的逻辑分支。

这三处逻辑分支涵盖了所有的可能出现的异常情况，先介绍下每个列含义，稍后我们分析下这三处逻辑分支。

Exception table 列的含义:

- from - to 表示字节码的偏移范围，如第一行记录所示，它定义了0-22偏移量的范围。
- target 表示如果出现异常，需要跳转到哪个偏移量进行处理。 如第一行所示，如果出现异常，需要跳转到偏移量33进行处理。
- type 表示需要匹配的异常类型，只有偏移量匹配并且类型匹配，才会通过target进行跳转。

下面分析下每一行的含义：
- 第一行： from-to偏移量为try代码块的内容，type为Exception，意思是如果try块中抛出Expception类型（包含子类型）的异常，那么跳转到catch中处理。
- 第二行：from-to偏移量为try代码块的内容，type为任何非Exception及子类的类型（第一行不匹配），那么直接跳转到finally（2）块,并且将异常用athrow重新抛出。
- 第三行：from-to偏移量为catch代码块的内容，type为任何类型，那么直接跳转到finally(2)代码块中，执行完后使用athrow重新抛出。


## 总结

本文整理了下Java字节码相关的基础知识，并通过三个场景（循环，条件，异常）来分析字节码。

## 参考资料
- [oracle-jvms8.pdf](https://docs.oracle.com/javase/specs/jvms/se8/jvms8.pdf)
- [java字节码指令集](https://www.cnblogs.com/vinozly/p/5399308.html)
