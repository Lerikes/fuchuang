package org.fuchuang.biz.userservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务启动入口
 */
@SpringBootApplication
@MapperScan("org.fuchuang.biz.userservice.dao.mapper")// 这样方便一点，不用每个mapper都去写注解
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
