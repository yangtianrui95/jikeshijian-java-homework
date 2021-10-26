package com.example.starter.configuration;

import com.example.starter.mybean.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnableGetStudentConfiguration {

    @Bean
    public Student student() {
        return Student.builder()
                .id(1)
                .name("stu")
                .build();
    }

}
