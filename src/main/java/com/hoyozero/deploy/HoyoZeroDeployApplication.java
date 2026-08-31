package com.hoyozero.deploy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
@MapperScan("com.hoyozero.deploy.mapper")
public class HoyoZeroDeployApplication {
    public static void main(String[] args) {
        SpringApplication.run(HoyoZeroDeployApplication.class, args);
    }
}
