package test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * 原始jdbc使用测试
 */
@Slf4j
//@SpringBootTest
public class JdbcTest {

    @Test
    @SneakyThrows
    public void insert() {
        final Properties properties = new Properties();
        properties.put("user" ,"root");
        final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/world", properties);
        log.info("connectionaaa: {}", connection.hashCode());
    }
}
