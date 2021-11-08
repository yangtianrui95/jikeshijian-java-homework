package com.example.mysqlshardtest.inserttest;

import com.example.mysqlshardtest.MysqlShardTestApplicationTests;
import com.example.mysqlshardtest.entity.Commodity;
import com.example.mysqlshardtest.entity.CommodityMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;

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
