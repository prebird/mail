package org.prebird.mailworker.service;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("message-queue-console")
@Slf4j
public class ConsoleMailService implements MailService {
  // 초당 5개로 요청 제한
  private RateLimiter rateLimiter = RateLimiter.create(3);

  /**
   * 메일 발송 로직을 콘솔 로그로 대체합니다.
   * google 메일 API 호출 평균시간인 4100 ms 만큼 sleep 합니다.
   * @param emailMessage
   */
  @Override
  public void send(EmailMessage emailMessage) {
    limitRequest(); // 요청 제한

    try {
      Thread.sleep(4100); // gmail 평균 메세지 처리 시간 만큼 sleep
      log.info(">>> mail sent!");
    } catch (Exception e) {
      log.error("에러 발생");
    }
  }

  private void limitRequest() {
    if (!rateLimiter.tryAcquire()) {
      throw new IllegalStateException("요청 제한: 나중에 다시 시도하세요");
    }
  }
}
