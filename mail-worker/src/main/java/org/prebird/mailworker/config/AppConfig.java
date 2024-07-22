package org.prebird.mailworker.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableAsync
@EnableScheduling
@Configuration
public class AppConfig {

  @Primary
  @Bean
  public ThreadPoolTaskExecutor mailWorkerTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(64);
    taskExecutor.setMaxPoolSize(64);
    taskExecutor.setPrestartAllCoreThreads(true); // 스레드풀 코어 수 만큼 미리 생성
    taskExecutor.setThreadNamePrefix("thread-pool-worker-");
    taskExecutor.initialize();
    return taskExecutor;
  }

  @Bean
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setPoolSize(2);
    return threadPoolTaskScheduler;
  }
}
