package com.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author: ddh
 * @Date: 2019/10/3 16:01
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.mall.item.mapper")
public class MallItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallItemApplication.class);
    }
}
