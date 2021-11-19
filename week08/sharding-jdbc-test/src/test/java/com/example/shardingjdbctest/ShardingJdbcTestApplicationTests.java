package com.example.shardingjdbctest;

import com.google.common.collect.ImmutableMap;
import com.oracle.tools.packager.mac.MacAppBundler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.UsesSunHttpServer;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@SpringBootTest
class ShardingJdbcTestApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void selectUser() {
        for (int i = 0; i < 100; i++) {
            jdbcTemplate.query("SELECT * FROM t_users where id = :id", ImmutableMap.<String, Object>builder()
                    .put("id", i)
                    .build(), (ResultSetExtractor<Object>) rs -> {
                        return rs.getString("name");
                    });
        }
    }

    @Test
    @SneakyThrows
    void insertUser() {
        for (int i = 2; i < 100; i++) {
            final Map<String, Object> map = ImmutableMap.<String, Object>builder()
                    .put("id", i)
                    .put("name", i+"name")
                    .put("password", "pwd")
                    .put("phoneNumber", "111")
                    .put("idcard", 111)
                    .put("money", 2.0)
                    .build();
            final int result = jdbcTemplate.update("INSERT INTO t_users VALUES (:id, :name, :password, :phoneNumber, :idcard, :money)",
                    map);
            log.info("result: {}", result);
        }
    }

}
