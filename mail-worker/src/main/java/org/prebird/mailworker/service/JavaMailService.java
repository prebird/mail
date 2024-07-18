package org.prebird.mailworker.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Profile({"real", "local-test"})
@Slf4j
@RequiredArgsConstructor
@Service
public class JavaMailService implements MailService{
  private final JavaMailSender javaMailSender;

  /**
   * Java Mail Sender 로 실제로 메일을 보냅니다.
   * @param emailMessage
   */
  @Override
  public void send(EmailMessage emailMessage) {
    long startTime = System.currentTimeMillis();

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(emailMessage.getToEmail());
      mimeMessageHelper.setSubject(emailMessage.getSubject());
      mimeMessageHelper.setText(emailMessage.getMessage(), true);
      javaMailSender.send(mimeMessage);

      // 시간 측정
      long endTime = System.currentTimeMillis();
      log.info("Execution mail send: {}", endTime - startTime);
    } catch (MessagingException e) {
      throw new RuntimeException("메일 발송 중 에러가 발생했습니다.", e);
    }
  }
}
