
10.（必做）研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：

代码在目录下的JdbcAPITest.java和JdbcBatchTests.java


### 1 JdbcApi 使用

获取连接池
```java
@SneakyThrows
    public JdbcTest() {
        final Properties properties = new Properties();
        properties.put("user", "root");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/world", properties);
        log.info("connectionaaa: {}", connection);
    }
```

插入

```java
 @Test
    @SneakyThrows
    public void insert() {
        try (final PreparedStatement preparedStatement
                     = connection.prepareStatement("INSERT INTO city (`Name`, `CountryCode`, `District`, `Population`)  VALUES ( ?,?,?,? )")) {
            preparedStatement.setString(1, "ytr");
            preparedStatement.setString(2, "AFG");
            preparedStatement.setString(3, "district");
            preparedStatement.setString(4, "111");
            final boolean result = preparedStatement.execute();
            log.info("result: {}", result);
        }
    }
```

### 2. batch/tx

```java

@Slf4j
public class JdbcBatchTests extends JdbcTest {

    @Test
    @SneakyThrows
    public void txTest() {
        try {
            connection.setAutoCommit(false);
            insertTest(true);
            log.info("commit");
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            log.info("rollback");
        } finally {
            connection.setAutoCommit(false);
            log.info("setAutoCommit false");
        }
    }

    @SneakyThrows
    private void insertTest(boolean mockException) {
        try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO city (`name`, `CountryCode`, `District`, `Population`) values ( ?,?,?,? )");) {
            for (int i = 0; i < 1000; i++) {
                statement.setString(1, "city" + i);
                statement.setString(2, "AFG");
                if (mockException && i == 500) {
                    statement.setString(3, "AAABBBBBBBBBBBCCCCCCCCCCCC");
                } else {
                    statement.setString(3, "AAA");
                }
                statement.setString(4, String.valueOf(i * i));

                statement.addBatch();
            }
            final int[] result = statement.executeBatch();
            log.info("batchTest: {}", Arrays.toString(result));
        }
    }


    @Test
    @SneakyThrows
    public void batchTest() {
        insertTest(false);
    }
}

```


##3. 配置Hikari

完成中