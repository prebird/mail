package org.prebird.shop.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.domain.EmailMessageRepository;
import org.prebird.shop.mail.domain.EmailType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 이메일 발송 로직 호출 시, DB에 저장하는 서비스입니다.
 */
@Profile("db")
@Slf4j
@RequiredArgsConstructor
@Service
public class DBMailService implements MailService{
  private final EmailMessageRepository emailMessageRepository;

  @Override
  public void send(EmailMessage emailMessage, EmailType emailType) {
    emailMessage.changeEmailType(emailType);
    emailMessageRepository.save(emailMessage);
    log.info(">>> emailMessage 메세지가 DB에 저장되었습니다.");
  }
}
