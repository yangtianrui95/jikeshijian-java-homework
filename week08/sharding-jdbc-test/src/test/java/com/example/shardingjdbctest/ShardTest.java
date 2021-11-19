package com.example.shardingjdbctest;


import com.example.shardingjdbctest.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@ActiveProfiles("shard")
@SpringBootTest
@Transactional
public class ShardTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void insertTest() {
        for (int i = 10; i < 1000; i++) {
            int finalI = i;
            final int result = jdbcTemplate.update(con -> {
                final PreparedStatement statement = con.prepareStatement("INSERT INTO t_order VALUES ( ?,? )");
                statement.setLong(1, finalI);
                statement.setString(2, String.valueOf(finalI * finalI));
                return statement;
            });
        }
        selectCount();
    }

    @Test
    public void selectCount() {
        final Long count = jdbcTemplate.query("SELECT COUNT(*) FROM t_order", new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                final boolean next = rs.next();
                if (next) {
                    return rs.getLong(1);
                }
                return null;
            }
        });
        log.info("count:{}", count);
    }


    @Test
    public void delete() {
        final int result = jdbcTemplate.update("DELETE FROM t_order");
        log.info("delete: {}", result);
    }

    @Test
    public void selectAll() {
        final List<Order> orderList = jdbcTemplate.query("SELECT * FROM t_order", (rs, rowNum) -> {
            final long orderId = rs.getLong("order_id");
            final String data = rs.getString("v_data");
            return new Order(orderId, data);
        });
        log.info("orderList:{}", orderList);
    }
}
