9.（必做）读写分离 - 动态切换数据源版本 1.0


## 继承AbsRoutingDataSource

```java

public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return Context.getSourceKey();
    }

    public static class Context  {
        private static final ThreadLocal<String> sourceKey = new ThreadLocal<>();

        public static void setSourceKey(String sourceKey) {
            Context.sourceKey.set(sourceKey);
        }

        public static String getSourceKey() {
            return Optional.ofNullable(Context.sourceKey.get()).orElse("masterDataSource");
        }

        public static void remove()  {
            sourceKey.remove();
        }
    }
}

```

## 配置DataSource
```java

/**
 * 主从路由配置
 * https://cloud.tencent.com/developer/article/1472124
 */
@Slf4j
@Configuration
public class DataSourceConfiguration {

    /**
     * 将配置参数绑定到DataSource对象中
     */
    @Bean("masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create()
                .build();
    }

    @Bean("slaveDataSource")
    @ConfigurationProperties(prefix = "spring.slave-datasource")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create()
                .build();
    }


    @Bean
    @Primary
    @SneakyThrows
    public DataSource dataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                 @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        log.info("master: {}, slave:{} ", masterDataSource.getConnection().getMetaData().getURL(),
                slaveDataSource.getConnection().getMetaData().getURL());
        final RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(
                ImmutableMap.builder()
                        .put("masterDataSource", masterDataSource)
                        .put("slaveDataSource", slaveDataSource)
                        .build());
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        return routingDataSource;
    }

}

```

### 添加切换注解

```java
 @Before("@annotation(RoutingWith)")
    public void around(JoinPoint joinPoint) {
        log.error("aop!!!!!!!!!");
        final String dataSourceKey = ((MethodSignature) joinPoint.getSignature())
                .getMethod().getAnnotation(RoutingWith.class).name();
        RoutingDataSource.Context.setSourceKey(dataSourceKey);
        RoutingDataSource.Context.remove();
    }

```
   

### 使用

```java
// 主数据源
@Test
@RoutingWith(name = "masterDataSource")
@SneakyThrows
public void masterDataSourceTest() {
    final String url = dataSource.getConnection().getMetaData().getURL();
    log.error("url: {}", url);
    Assertions.assertThat(url).contains("jdbc:mysql://localhost:3307/commerce");
}

// 从数据源
@Test
@RoutingWith(name = "slaveDataSource")
@SneakyThrows
public void slaveDataSourceTest() {
    RoutingDataSource.Context.setSourceKey("slaveDataSource");
    final String url = dataSource.getConnection().getMetaData().getURL();
    log.error("url: {}", url);
    log.error("key: , {}", context.getBean(RoutingWithAdvices.class));
}
```

