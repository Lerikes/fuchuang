package org.fuchuang.biz.passageservice.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置类
 */
@EnableAsync
@Configuration
public class ThreadPoolConfig {

    /**
     * 线程工厂
     */
    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNamePrefix("mongoDB写入线程池")
            .build();

    /**
     * 用于 mongoDB 写入的线程池 IO密集型
     * @return ThreadPoolExecutor
     */
    @Bean
    public ThreadPoolExecutor mongoThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(), // 使用CPU核心数来作为核心线程数
                Runtime.getRuntime().availableProcessors() * 2, // 最大线程数是CPU核心线程的两倍
                60L, // 空闲线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new LinkedBlockingDeque<>(10), // 任务队列采用阻塞队列实现，并且线程队列容量为10
                threadFactory, // 自定义的线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy() // CallerRunsPolicy 策略表示由提交任务的线程来运行该任务（通常是调用线程），相当于把任务回退给调用方，从而避免任务丢失或过载。
        );
    }
}
