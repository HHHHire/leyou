package com.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author: ddh
 * @data: 2020/4/2 1:38
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.mall.user.mapper")
public class MallUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallUserApplication.class);
    }
}
