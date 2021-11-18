2.（必做）设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github。


修改server.yml配置文件

```yaml

rules:
  - !AUTHORITY
    users:
      # 配置账户和密码
      - root@%:root
      - sharding@:sharding
    provider:
      type: NATIVE

props:
  max-connections-size-per-query: 1
  executor-size: 16  # Infinite by default.
  proxy-frontend-flush-threshold: 128  # The default value is 128.
    # LOCAL: Proxy will run with LOCAL transaction.
    # XA: Proxy will run with XA transaction.
    # BASE: Proxy will run with B.A.S.E transaction.
  proxy-transaction-type: LOCAL
  xa-transaction-manager-type: Atomikos
  proxy-opentracing-enabled: false
  proxy-hint-enabled: false
  sql-show: false
  check-table-metadata-enabled: false
  lock-wait-timeout-milliseconds: 50000 # The maximum time to wait for a lock
```

配置config.sharding.yml文件

```yaml

schemaName: sharding_db

dataSources:
# 两个数据源
  ds_0:
    url: jdbc:mysql://mysql2:3306/sharding1?serverTimezone=UTC&useSSL=false
    username: root
    password: 
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50
    minPoolSize: 1
    maintenanceIntervalMilliseconds: 30000
  ds_1:
    url: jdbc:mysql://mysql:3306/sharding2?serverTimezone=UTC&useSSL=false
    username: root
    password: 
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50
    minPoolSize: 1
    maintenanceIntervalMilliseconds: 30000

rules:
- !SHARDING
  tables:
#    逻辑表名称
    t_order:
      actualDataNodes: ds_${0..1}.t_order${0..1}
      tableStrategy:
        standard:
          shardingColumn: order_id
          shardingAlgorithmName: t_order_inline
      keyGenerateStrategy:
        column: order_id
        keyGeneratorName: snowflake
  bindingTables:
    - t_order
  defaultDatabaseStrategy:
    standard:
      shardingColumn: order_id
      shardingAlgorithmName: database_inline
  
#  分表与分库算法
  shardingAlgorithms:
    t_order_inline:
      type: INLINE
      props:
        algorithm-expression: t_order${order_id % 2}
    database_inline:  
      type: INLINE
      props: 
        algorithm-expression: ds_${order_id % 2}

  
  keyGenerators:
    snowflake:
      type: SNOWFLAKE
      props:
        worker-id: 123
```

保证两个MySQL都可以已经启动

使用docker 启动sharding-proxy

```java
docker run -d -v /Users/yangtianrui/docker-app/sharding-proxy/conf:/opt/shardingsphere-proxy/conf -v /Users/yangtianrui/docker-app/sharding-proxy/ext-lib/:/opt/shardingsphere-proxy/ext-lib  -p 33007:3307 --link mysql --link mysql2 --name sharding-proxy apache/sharding-proxy:latest
```

查看日志启动成功

```
[INFO ] 2021-11-17 16:40:46.934 [main] o.a.s.p.i.i.AbstractBootstrapInitializer - Database name is `MySQL`, version is `5.7.36`
[INFO ] 2021-11-17 16:40:47.501 [main] o.a.s.p.frontend.ShardingSphereProxy - ShardingSphere-Proxy start success.```
```

使用MySql连接即可