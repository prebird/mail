package org.prebird.mailworker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@DependsOn("threadPoolTaskExecutor")
public class CheckController {
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

  @GetMapping("/thread-pool")
  public void logThreadPoolCount() {
    // 스레드 풀의 현재 스레드 갯수
    log.info("threadPoolTaskExecutor : {}", threadPoolTaskExecutor.getPoolSize());
  }
}
