package com.huike.video.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置
 * 用于创作模块的异步下载等操作
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 创作任务专用线程池
     * 用于异步下载图片/视频/封面等耗时操作
     */
    @Bean("creationTaskExecutor")
    public Executor creationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：CPU 核心数
        executor.setCorePoolSize(2);
        // 最大线程数
        executor.setMaxPoolSize(5);
        // 队列容量
        executor.setQueueCapacity(100);
        // 线程名前缀
        executor.setThreadNamePrefix("creation-async-");
        // 拒绝策略：由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("创作任务线程池初始化完成: core={}, max={}, queue={}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize(), 100);
        return executor;
    }
}
