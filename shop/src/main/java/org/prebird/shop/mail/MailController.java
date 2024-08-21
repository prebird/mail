package org.prebird.shop.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.domain.EmailType;
import org.prebird.shop.mail.service.MockServerMailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class MailController {
  private final MockServerMailService mockServerMailService;

  /**
   * 메일 비동기 성능 테스트를 위한 API
   * @param loadTestResultId
   */
  @PostMapping("/load-test/send-mail")
  public void sendMail(@RequestParam Long loadTestResultId) {
    mockServerMailService.send(EmailMessage.builder()
            .subject("load-test")
            .toEmail("example@example.com")
            .emailType(EmailType.NORMAL)
        .build(), loadTestResultId);
  }
}
