package org.prebird.shop;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.domain.EmailMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("mock-mail")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {"GMAIL_PASSWORD="})
public class AsyncMailLoadTest {
  @LocalServerPort
  private int port;

  @Autowired
  private EmailMessageRepository emailMessageRepository;

  @Autowired
  private TestRestTemplate testRestTemplate;

  private static TaskExecutor taskExecutor = testMailTaskExecutor();

  private AtomicLong successCount = new AtomicLong(0L);
  private AtomicLong errorCount = new AtomicLong(0L);
  List<CompletableFuture<Void>> futures = new ArrayList<>();

  @BeforeEach
  void init() {
    emailMessageRepository.deleteAll();
    successCount = new AtomicLong(0L);
    errorCount = new AtomicLong(0L);
    futures = new ArrayList<>();
  }

  @Test
  void 주문생성요청_을_동시에_vUser_만큼_전송합니다() {
    // given
    int vUsers = 6;
    // when
    for (int i = 0; i < vUsers; i++) {
      requestCreateOrderAsync();      // CompletableFuture 를 사용하여 동시에 요청을 보냄
    }

    // 모든 작업이 완료되기를 기다림
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .exceptionally(ex -> null)  // 예외 무시
        .join();

    // then
    List<EmailMessage> sentEmail = emailMessageRepository.findBySentAtIsNotNull();
    assertThat(sentEmail).hasSize(vUsers);
    log.info("All emails processed.");
    log.info("Success count: " + successCount.get());
    log.info("Error count: " + errorCount.get());
  }

  private void requestCreateOrderAsync() {
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        requestCreateOrder();
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

  private void requestCreateOrder() {
    String url = "http://localhost:" + port + "/orders?username=tester01&productId=1";
    testRestTemplate.postForEntity(url, null, Void.class);
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
