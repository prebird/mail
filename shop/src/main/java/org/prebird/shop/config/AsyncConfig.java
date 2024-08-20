package org.prebird.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
  @Value("${mail.async.core-pool-size}")
  private Integer mailCorePoolSize;
  @Value("${mail.async.max-pool-size}")
  private Integer mailMaxPoolSize;
  @Value("${mail.async.queue-size}")
  private Integer mailQueueSize;

  @Primary
  @Bean
  public ThreadPoolTaskExecutor mailServiceTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(mailCorePoolSize);
    taskExecutor.setMaxPoolSize(mailMaxPoolSize);
    taskExecutor.setQueueCapacity(mailQueueSize);
    taskExecutor.setPrestartAllCoreThreads(true); // 스레드풀 코어 수 만큼 미리 생성
    taskExecutor.setThreadNamePrefix("mailExecutor-");
    taskExecutor.initialize();
    return taskExecutor;
  }
}
