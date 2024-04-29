package com.longtao;


import com.longtao.service.MigrationService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;

@MapperScan("com.longtao.mapper")
@EnableConfigurationProperties
@SpringBootApplication
public class App {
    @Autowired
    private MigrationService migrationService;
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @PostConstruct
    public void init() {
        migrationService.migrationToPostgres();;
    }
}