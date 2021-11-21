package com.arslinth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.arslinth.dao")
@EnableAsync // 开启@Async注解
@EnableRetry // 开启@Retryable注解 重试功能
public class ArslinthBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArslinthBootApplication.class, args);
    }

}
