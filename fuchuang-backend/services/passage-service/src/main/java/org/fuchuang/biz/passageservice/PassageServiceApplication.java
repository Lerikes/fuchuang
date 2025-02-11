package org.fuchuang.biz.passageservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 文章服务启动入口
 */
@SpringBootApplication
@MapperScan("org.fuchuang.biz.passageservice.dao.mapper")
public class PassageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassageServiceApplication.class, args);
    }
}
