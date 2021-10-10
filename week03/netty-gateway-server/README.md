
# netty-gateway-server

## 运行

编译
```java
mvn clean package -Dmaven.test.skip=true
```

运行
```java
java -jar /Users/yangtianrui/github/jikeshijian-java-homework/week03/netty-gateway-server/target/jvm_test-1.0.jar 
```

## 示例

### Netty直接返回Http响应

代码在LocalResultRouteAction这个类。

```java
curl -v http://0.0.0.0:8808/hello
```

返回值
```
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8808 (#0)
> GET /hello HTTP/1.1
> Host: 0.0.0.0:8808
> User-Agent: curl/7.64.1
> Accept: */*
>
< HTTP/1.1 200 OK
< HttpAddHeaderFilter-AddResponseHeader: NettyGateway
* no chunk, no close, no size. Assume close to signal end
<
* Closing connection 0
hello-content%
```

### 代理到HttpClient返回Http响应

```java
curl -v http://0.0.0.0:8808/httpProxy/baidu
```

返回值
```
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8808 (#0)
> GET /httpProxy/baidu HTTP/1.1
> Host: 0.0.0.0:8808
> User-Agent: curl/7.64.1
> Accept: */*
>
< HTTP/1.1 200 OK
< HttpAddHeaderFilter-AddResponseHeader: NettyGateway
* no chunk, no close, no size. Assume close to signal end
<
<html>
<head>
	<script>
		location.replace(location.href.replace("https://","http://"));
	</script>
</head>
<body>
	<noscript><meta http-equiv="refresh" content="0;url=http://www.baidu.com/"></noscript>
</body>
* Closing connection 0
</html>%
```
### 代理到NettyClient返回Http响应

```java
curl -v http://0.0.0.0:8808/nettyProxy/gateway
```

返回值
```
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8808 (#0)
> GET /nettyProxy/gateway HTTP/1.1
> Host: 0.0.0.0:8808
> User-Agent: curl/7.64.1
> Accept: */*
>
< HTTP/1.1 200
< Content-Type: text/plain;charset=UTF-8
< Content-Length: 2
< Date: Sun, 10 Oct 2021 08:17:59 GMT
< HttpAddHeaderFilter-AddResponseHeader: NettyGateway
<
* Connection #0 to host 0.0.0.0 left intact
ok* Closing connection 0
```
