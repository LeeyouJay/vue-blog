package com.arslinth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.arslinth.dao")
@EnableAsync // 开启@Async注解
public class ArslinthBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArslinthBootApplication.class, args);
    }

}
