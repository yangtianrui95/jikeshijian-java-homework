package com.example.mysqlshardtest.inserttest;

import com.example.mysqlshardtest.MysqlShardTestApplicationTests;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

@Slf4j
@Transactional(rollbackFor = Exception.class)
public class JdbcTemplateInsertTest extends MysqlShardTestApplicationTests {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private Connection connection;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @SneakyThrows
    public void setup() {
        log.info("setup");
        final Driver driver = DriverManager.getDriver(jdbcUrl);
        final Properties properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        connection = driver.connect(jdbcUrl, properties);
    }

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

    @Test
    @SneakyThrows
    public void insertBatch() {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Connection connection = DataSourceUtils.getConnection(dataSource);
//        connection.setAutoCommit(false);
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
//        connection.commit();
        stopWatch.stop();
        log.error("cost: {}", stopWatch.getLastTaskTimeMillis());
    }
}
