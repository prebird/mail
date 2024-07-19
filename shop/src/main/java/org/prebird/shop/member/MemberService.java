package org.prebird.shop.member;

import lombok.RequiredArgsConstructor;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.domain.EmailType;
import org.prebird.shop.mail.service.MailService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
  private final MailService mailService;

  public void sendVerifyMail(String email) {
    mailService.send(EmailMessage.builder()
        .toEmail(email)
        .subject("본인인증메일")
        .message("<h1>본인 인증 메일입니다.</h1>")
        .emailType(EmailType.URGENT)
        .build());
  }
}
