package org.prebird.shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.domain.EmailType;
import org.prebird.shop.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("real")
@SpringBootTest(properties = {
    "GMAIL_PASSWORD="
})
public class AsyncMailLoadTest {
  @Autowired
  private MailService mailService;

  private static TaskExecutor taskExecutor;

  static {
    taskExecutor = testMailTaskExecutor();
  }

  private AtomicLong successCount = new AtomicLong(0L);
  private AtomicLong errorCount = new AtomicLong(0L);
  List<CompletableFuture<Void>> futures = new ArrayList<>();

  @Test
  void loadTest() {
    Long vUsers = 30L;
    EmailMessage emailMessage = EmailMessage.builder().toEmail("iopengom@naver.com")
        .message("테스트 메세지")
        .subject("테스트 메세지").build();

    for (int i = 0; i < vUsers; i++) {
      sendMailAsync(emailMessage);      // CompletableFuture 를 사용하여 동시에 요청을 보냄
    }

    // 모든 작업이 완료되기를 기다림
    CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    allFutures.handle((result, ex) -> {   // whenComplete 는 예외를 밖으로 던짐, handle 은 응답을 정해줘야함, exceptionally() 는 예외 발생시에만 동작함
      log.info("All emails processed.");
      log.info("Success count: " + successCount.get());
      log.info("Error count: " + errorCount.get());
      return null; // 정상응답
    }).join(); // 메인 스레드에서 실행을 기다리게 하기 위해 사용
  }

  private void sendMailAsync(EmailMessage emailMessage) {
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
      mailService.send(emailMessage, EmailType.NORMAL);
    }, taskExecutor).whenComplete((result, ex) -> {
      if (ex != null) {
        log.error("## error!", ex);
        errorCount.incrementAndGet();
      } else {
        log.info("sent At: {}", LocalDateTime.now());
        successCount.incrementAndGet();
      }
    });
    futures.add(future);
  }

  private static TaskExecutor testMailTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(30);
    executor.setMaxPoolSize(30);
    executor.setQueueCapacity(100);
    executor.setPrestartAllCoreThreads(true);
    executor.setThreadNamePrefix("testMail-");
    executor.initialize();
    return executor;
  }
}
