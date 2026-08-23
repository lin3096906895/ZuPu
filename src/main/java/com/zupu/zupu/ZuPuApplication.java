package com.zupu.zupu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zupu.zupu.mapper")
public class ZuPuApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuPuApplication.class, args);
    }

}
