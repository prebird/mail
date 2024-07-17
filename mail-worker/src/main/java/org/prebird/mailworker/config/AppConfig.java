package org.prebird.mailworker.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@EnableScheduling
@Configuration
public class AppConfig {

  @Bean
  public Executor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(32);
    taskExecutor.setMaxPoolSize(32);
    taskExecutor.setPrestartAllCoreThreads(true); // 스레드풀 코어 수 만큼 미리 생성
    taskExecutor.initialize();
    return taskExecutor;
  }
}
