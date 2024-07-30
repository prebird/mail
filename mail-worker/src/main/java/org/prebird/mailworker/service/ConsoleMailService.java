package org.prebird.mailworker.service;

import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("message-queue-console")
@Slf4j
public class ConsoleMailService implements MailService {

  /**
   * 메일 발송 로직을 콘솔 로그로 대체합니다.
   * google 메일 API 호출 평균시간인 4100 ms 만큼 sleep 합니다.
   * @param emailMessage
   */
  @Override
  public void send(EmailMessage emailMessage) {
    try {
      Thread.sleep(4100);
      log.info(">>> mail sent!");
    } catch (Exception e) {
      log.error("에러 발생");
    }
  }
}
