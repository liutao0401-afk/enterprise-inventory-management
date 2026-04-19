package com.eims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * EIMS 后端启动类
 * Enterprise Inventory Management System
 */
@EnableScheduling
@SpringBootApplication
public class EimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EimsApplication.class, args);
    }
}
