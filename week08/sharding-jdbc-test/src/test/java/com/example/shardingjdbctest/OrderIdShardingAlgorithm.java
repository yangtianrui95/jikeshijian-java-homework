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
