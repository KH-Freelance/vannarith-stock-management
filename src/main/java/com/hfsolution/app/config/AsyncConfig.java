package com.hfsolution.app.config;


import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig {

  @Bean(name = "backgroundTaskExecutor")
  Executor backgroundTaskExecutor() {
    var backgroundTaskExecutor = new ThreadPoolTaskExecutor();
    backgroundTaskExecutor.setTaskDecorator(new ContextCopyingDecorator());
    backgroundTaskExecutor.setAwaitTerminationSeconds(2);
    backgroundTaskExecutor.initialize();
    return backgroundTaskExecutor;
  }

  @Bean
  @Primary
  Executor taskExecutor() {
    var taskExecutor = new ThreadPoolTaskExecutor();
    // Can execute 20 tasks at the same time.
    taskExecutor.setCorePoolSize(20);
    taskExecutor.setThreadNamePrefix("GBL-THD-");
    taskExecutor.setQueueCapacity(1000);
    taskExecutor.setMaxPoolSize(10000);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(false);
    // Maximum time to complete task
    taskExecutor.setAwaitTerminationSeconds(10);
    // Allow to terminate idle thread 
    taskExecutor.setAllowCoreThreadTimeOut(true);
    // Wait for execute new task duration before terminate.
    taskExecutor.setKeepAliveSeconds(10);
    taskExecutor.initialize();
    return taskExecutor;
  }

  @Bean("jpaExecutor")
  Executor taskExecutorForJpa() {
    var taskExecutor = new ThreadPoolTaskExecutor();
    // Can execute 50 tasks at the same time.
    taskExecutor.setCorePoolSize(50);
    taskExecutor.setThreadNamePrefix("PJA-THD-");
    taskExecutor.setQueueCapacity(1000);
    taskExecutor.setMaxPoolSize(10000);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(false);
    // Maximum time to complete task
    taskExecutor.setAwaitTerminationSeconds(10);
    // Allow to terminate idle thread 
    taskExecutor.setAllowCoreThreadTimeOut(true);
    // Wait for execute new task duration before terminate.
    taskExecutor.setKeepAliveSeconds(10);
    taskExecutor.initialize();
    return taskExecutor;
  }
}