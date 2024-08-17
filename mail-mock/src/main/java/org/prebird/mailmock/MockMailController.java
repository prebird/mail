package org.prebird.mailmock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailmock.service.MailRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 성능 테스트 용도로 Mail Provider (Gmail, AWS SES 등) 서버의 동작을 Mocking 합니다.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class MockMailController {
  private final MailRateLimiter mailRateLimiter;

  /**
   * 메일을 처리하는 동작을 Mocking 합니다.
   * - email 계정별 Rate Limit 적용 (초당 5건 제한)
   * - 실제 처리 시간 만큼 sleep
   * @param account 이메일 계정
   */
  @PostMapping("/process-mail")
  public ResponseEntity<Void> processMail(String account) {
    if (!mailRateLimiter.limit(account)) {
      return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
    sleep(4000L); // gmail 평균 메세지 처리 시간 만큼 sleep
    return ResponseEntity.ok().build();
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
