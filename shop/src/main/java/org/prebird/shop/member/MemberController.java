package org.prebird.shop.member;

import lombok.RequiredArgsConstructor;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.domain.EmailType;
import org.prebird.shop.mail.service.MailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MailService mailService;

  /**
   * 본인 인증 메일 발송
   */
  @PostMapping("/send-verify-mail")
  public void sendVerifyMail(@RequestParam String email) {
    mailService.send(EmailMessage.builder()
            .toEmail(email)
            .subject("본인인증메일")
            .message("<h1>본인 인증 메일입니다.</h1>")
            .emailType(EmailType.URGENT)
        .build());
  }
}
