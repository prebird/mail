package org.prebird.mailmock;

import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 성능 테스트 용도로 Mail Provider (Gmail, AWS SES 등) 서버의 동작을 Mocking 합니다.
 */
@Slf4j
@RestController
public class MockMailController {
  // 초당 5개로 요청 제한
  private static final int LIMIT_COUNT = 5;
  private final ConcurrentMap<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();
  private RateLimiter tempRateLimiter = RateLimiter.create(LIMIT_COUNT);

  /**
   * 메일을 처리하는 동작을 Mocking 합니다.
   * - email 계정별 Rate Limit 적용 (초당 5건 제한)
   * - 실제 처리 시간 만큼 sleep
   * @param account 이메일 계정
   */
  @PostMapping("/process-mail")
  public ResponseEntity<Void> processMail(String account) {
    if (!limitRequest(account)) {
      return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
    sleep(4000L); // gmail 평균 메세지 처리 시간 만큼 sleep
    return ResponseEntity.ok().build();
  }

  // 요청 제한
  private boolean limitRequest(String account) {
//    if (!tempRateLimiter.tryAcquire()) {
//      return false;
//    }
//    return true;

    RateLimiter rateLimiter = rateLimiters.putIfAbsent(account, createRateLimiter());
    if (rateLimiter.tryAcquire()) {
      return true;
    }
    return false;
  }

  private RateLimiter createRateLimiter() {
    return RateLimiter.create(LIMIT_COUNT); // 초당 5개의 요청 허용
  }

  private static void sleep(Long millis) {
    try {
      Thread.sleep(millis);
      log.info(">>> 메일 처리 완료");
    } catch (InterruptedException e) {
      log.error("에러 발생");
      throw new RuntimeException("sleep 에러");
    }
  }
}
