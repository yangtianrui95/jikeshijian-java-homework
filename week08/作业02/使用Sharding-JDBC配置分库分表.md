2.（必做）设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github。


分库分表配置
```properties

spring.shardingsphere.datasource.names=ds0,ds1

spring.shardingsphere.datasource.ds0.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds0.jdbc-url=jdbc:mysql://localhost:3309/sharding1
spring.shardingsphere.datasource.ds0.username=root
spring.shardingsphere.datasource.ds0.password=
spring.shardingsphere.datasource.ds0.maxPoolSize=50

spring.shardingsphere.datasource.ds1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds1.jdbc-url=jdbc:mysql://localhost:3307/sharding2
spring.shardingsphere.datasource.ds1.username=root
spring.shardingsphere.datasource.ds1.password=
spring.shardingsphere.datasource.ds1.maxPoolSize=50

spring.shardingsphere.sharding.binding-tables=t_order
spring.shardingsphere.sharding.tables.t_order.actual-data-nodes=ds${0..1}.t_order${0..1}
spring.shardingsphere.sharding.tables.t_order.table-strategy.standard.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order.table-strategy.standard.precise-algorithm-class-name=com.example.shardingjdbctest.OrderIdShardingAlgorithm
spring.shardingsphere.sharding.default-database-strategy.standard.sharding-column=order_id
spring.shardingsphere.sharding.default-database-strategy.standard.precise-algorithm-class-name=com.example.shardingjdbctest.OrderIdShardingAlgorithm
```

编写分库分表算法

```java
package com.example.shardingjdbctest;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

@Slf4j
public class OrderIdShardingAlgorithm implements PreciseShardingAlgorithm<Long> {
    public OrderIdShardingAlgorithm() {
        log.info("OrderIdShardingAlgorithm");
    }

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        log.info("doSharding:{}, shardingValue: {}", availableTargetNames, shardingValue);
        for (String s : availableTargetNames) {
            String value = String.valueOf(shardingValue.getValue() % availableTargetNames.size());
            if (s.endsWith(value)) {
                return s;
            }
        }
        return null;
    }
}
```

编写测试用例

```java
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

```

