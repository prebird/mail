package org.prebird.shop.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.domain.EmailType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("console")
@Service
public class ConsoleMailService implements MailService{

  /**
   * 메일 발송 로직을 콘솔 로그로 대체합니다.
   * google 메일 API 호출 평균시간인 4100 ms 만큼 sleep 합니다.
   * @param emailMessage
   */
  @Override
  public void send(EmailMessage emailMessage, EmailType emailType) {
    try {
      Thread.sleep(4100);
      log.info(">>> mail sent!");
    } catch (Exception e) {
      log.error("에러 발생");
    }
  }

  @Override
  public void send(EmailMessage emailMessage) {
    send(emailMessage, EmailType.NORMAL);
  }
}
