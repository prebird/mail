package org.prebird.shop.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.mail.EmailMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("console")
@Service
public class ConsoleMailService implements MailService{

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
