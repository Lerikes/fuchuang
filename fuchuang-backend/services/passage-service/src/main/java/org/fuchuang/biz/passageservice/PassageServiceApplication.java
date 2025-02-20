package org.fuchuang.biz.passageservice;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 文章服务启动入口
 */
@SpringBootApplication
@EnableFileStorage // 开启文件存储
@EnableDiscoveryClient
@EnableAsync
@EnableTransactionManagement
@EnableFeignClients(basePackages = "org.fuchuang.biz.passageservice.remote")
@MapperScan("org.fuchuang.biz.passageservice.dao.mapper")
public class PassageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassageServiceApplication.class, args);
    }
}
