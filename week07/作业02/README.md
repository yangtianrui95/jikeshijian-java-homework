
2. （必做）按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率

## 方式1
使用jdbc的方式插入
```java
    @Test
    @SneakyThrows
    public void insertBatch() {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        connection.setAutoCommit(false);
        @Cleanup
        final PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO t_commodity2 " +
                        "( id, `name`, `desc`, price, create_time, update_time, shop_id )" +
                        " VALUES ( ?, ? ,?, ?, ? , ?, ? )");
        for (int i = 0; i < 10_0000; i++) {
            preparedStatement.setInt(1, i + 1);
            preparedStatement.setString(2, "name" + i);
            preparedStatement.setString(3, "desc" + i);
            preparedStatement.setDouble(4, 1.0d);
            preparedStatement.setLong(5, System.currentTimeMillis());
            preparedStatement.setLong(6, System.currentTimeMillis());
            preparedStatement.setInt(7, 1);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        connection.commit();
        stopWatch.stop();
        log.error("cost: {}", stopWatch.getLastTaskTimeMillis());
    }

```

## 方式2 
使用JdbcTemplate插入

```java
@Test
public void jdbcTemplateInsertBatch() {
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    jdbcTemplate.batchUpdate("INSERT INTO t_commodity2 " +
            "( id, `name`, `desc`, price, create_time, update_time, shop_id )" +
            " VALUES ( ?, ? ,?, ?, ? , ?, ? )", new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setInt(1, i + 1);
            ps.setString(2, "name" + i);
            ps.setString(3, "desc" + i);
            ps.setDouble(4, 1.0d);
            ps.setLong(5, System.currentTimeMillis());
            ps.setLong(6, System.currentTimeMillis());
            ps.setInt(7, 1);
        }

        @Override
        public int getBatchSize() {
            return 10_0000;
        }
    });
    stopWatch.stop();
    log.error("cost: {}", stopWatch.getLastTaskTimeMillis());
}
```


## 方式3 
使用MyBatis插入
```java

@Slf4j
@Transactional
public class MyBatisInsertTest extends MysqlShardTestApplicationTests {

    @Autowired
    private CommodityMapper commodityMapper;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void insertBatch() {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 10_0000; i++) {
            commodityMapper.insert(Commodity.builder()
                    .id(i)
                    .name("name" + i)
                    .desc("desc" + i)
                    .createTime(System.currentTimeMillis())
                    .updateTime(System.currentTimeMillis())
                    .price(new BigDecimal("1.0"))
                    .shopId(1)
                    .build());
        }
        stopWatch.stop();
        final long cost = stopWatch.getLastTaskTimeMillis();
        log.error("cost: {}", cost);
    }
}
```


## 性能比较

使用Jdbc和JdbcTemplate性能较好，原因是使用batchUpdate批量提交，MySQL Server端批量解析性能较好。

使用Mybatis插入性能较差，原因是MySQL解析SQL次数较多。