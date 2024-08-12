package org.prebird.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
  @Primary
  @Bean
  public ThreadPoolTaskExecutor mailServiceTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(10);
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.setPrestartAllCoreThreads(true); // 스레드풀 코어 수 만큼 미리 생성
    taskExecutor.setThreadNamePrefix("mailExecutor-");
    taskExecutor.initialize();
    return taskExecutor;
  }
}
