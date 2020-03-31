package com.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author: ddh
 * @data: 2020/3/24 20:34
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MallUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallUploadApplication.class);
    }
}
