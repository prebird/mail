package org.prebird.mailmock;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@Slf4j
@SpringBootTest
class MockMailControllerTest {
  @Autowired
  private MockMailController mockMailController;

  private AtomicLong successCount = new AtomicLong(0L);
  private AtomicLong errorCount = new AtomicLong(0L);
  List<CompletableFuture<Void>> futures = new ArrayList<>();

  @BeforeEach
  void initCount() {
    successCount = new AtomicLong(0L);
    errorCount = new AtomicLong(0L);
  }

  @Test
  public void 초당_5건_이하의_요청은_제한되지_않습니다() {
    int requestCount = 4;
    String account = "test@example.com";

    for (int i = 0; i < requestCount; i++) {
      callProcessMailAsync(account);
    }

    // 모든 작업이 완료되기를 기다림
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .exceptionally(ex -> null)  // 예외 무시
        .join();

    assertThat(errorCount.longValue()).isZero();
    log.info("All emails processed.");
    log.info("Success count: " + successCount.get());
    log.info("Error count: " + errorCount.get());
  }

  @Test
  public void 초당_5건_이상의_요청은_제한됩니다() {
    int requestCount = 5;
    String account = "test@example.com";

    for (int i = 0; i < requestCount; i++) {
      callProcessMailAsync(account);
    }

    // 모든 작업이 완료되기를 기다림
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .exceptionally(ex -> null)  // 예외 무시
        .join();

    assertThat(errorCount.longValue()).isNotZero();
    log.info("All emails processed.");
    log.info("Success count: " + successCount.get());
    log.info("Error count: " + errorCount.get());
  }

  private void callProcessMailAsync(String account) {
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
      ResponseEntity<Void> response = mockMailController.processMail(account);
      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new RuntimeException(response.getStatusCode().toString());
      }
    }).whenComplete((result, ex) -> {
      if (ex != null) {
        log.error("err ", ex);
        errorCount.incrementAndGet();
      } else {
        successCount.incrementAndGet();
      }
    });
    futures.add(future);
  }
}
