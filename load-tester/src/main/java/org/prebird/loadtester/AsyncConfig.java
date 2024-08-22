package org.prebird.loadtester;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
  @Bean
  public TaskExecutor loadTestTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(30);
    executor.setMaxPoolSize(30);
    executor.setQueueCapacity(100);
    executor.setPrestartAllCoreThreads(true);
    executor.setThreadNamePrefix("loadTest-");
    executor.initialize();
    return executor;
  }
}
