package com.learn.java.ai.langchain4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.learn.java.ai.langchain4j.mapper")
public class XiaozhiApp {

    public static void main(String[] args) {
        SpringApplication.run(XiaozhiApp.class, args);
    }
}