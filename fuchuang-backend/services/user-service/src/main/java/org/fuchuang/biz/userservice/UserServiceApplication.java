package org.fuchuang.biz.userservice;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动入口
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFileStorage
@MapperScan("org.fuchuang.biz.userservice.dao.mapper")// 这样方便一点，不用每个mapper都去写注解
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
