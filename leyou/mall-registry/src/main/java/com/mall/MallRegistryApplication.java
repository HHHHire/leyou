package com.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Author: ddh
 * @Date: 2019/10/2 21:34
 * @Description:
 */
@SpringBootApplication
@EnableEurekaServer
public class MallRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallRegistryApplication.class);
    }
}
