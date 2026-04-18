package com.company.clawboard.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final ClawboardProperties properties;

    @Bean("scanExecutor")
    public Executor scanExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getScan().getThreadPoolSize());
        executor.setMaxPoolSize(properties.getScan().getThreadPoolSize());
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("scan-");
        executor.initialize();
        return executor;
    }
}
