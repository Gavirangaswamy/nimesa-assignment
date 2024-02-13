package com.nimesa.nimesaorchestrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorServiceConfiguration {

    @Value("${pool.core.size}")
    private int coreSize;

    @Value("${pool.max.size}")
    private int maxSize;

    @Value("${pool.queue.size}")
    private int queueSize;

    @Bean
    public ExecutorService executorService() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        return new ThreadPoolExecutor(coreSize, maxSize, 60L, TimeUnit.SECONDS, queue);
    }
}
