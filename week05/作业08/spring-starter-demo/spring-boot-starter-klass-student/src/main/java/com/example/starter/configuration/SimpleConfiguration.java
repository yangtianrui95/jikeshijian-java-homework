package com.example.starter.configuration;

import com.example.starter.mybean.Klass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SimpleConfiguration {

    public SimpleConfiguration() {
        log.info("SimpleConfiguration create!!!");
    }

    @Bean
    public Klass klass() {
        return new Klass();
    }
}
