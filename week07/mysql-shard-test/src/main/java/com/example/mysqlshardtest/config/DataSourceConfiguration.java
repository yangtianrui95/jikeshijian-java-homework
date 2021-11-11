package com.example.mysqlshardtest.config;

import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

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
