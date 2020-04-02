package com.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: ddh
 * @data: 2020/3/31 19:32
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MallGoodsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallGoodsWebApplication.class);
    }
}
