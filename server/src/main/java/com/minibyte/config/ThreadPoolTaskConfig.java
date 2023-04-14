package com.minibyte.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: MiniByte
 * @date: 2022/5/30
 * @description:
 */
@Configuration
@EnableScheduling
@Slf4j
public class ThreadPoolTaskConfig {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(50);
//        executor.setMaxPoolSize(1000);
//        executor.setQueueCapacity(200);
//        executor.setKeepAliveSeconds(100);
//        executor.setThreadNamePrefix("asyncExecutor-");
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.initialize();
//        return executor;
        log.info("init executor successful......");
        return new ThreadPoolTaskScheduler();
    }

    // TransmittableThreadLocal 是Alibaba开源的、用于解决 “在使用线程池等会缓存线程的组件情况下传递ThreadLocal” 问题的 InheritableThreadLocal 扩展。

}
