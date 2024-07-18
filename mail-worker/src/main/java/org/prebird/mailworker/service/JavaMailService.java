package org.prebird.mailworker.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Slf4j
@RequiredArgsConstructor
public class JavaMailService implements MailService{
  private final JavaMailSender normalJavaMailSender;

  /**
   * Java Mail Sender 로 실제로 메일을 보냅니다.
   * @param emailMessage
   */
  @Override
  public void send(EmailMessage emailMessage) {
    long startTime = System.currentTimeMillis();

    MimeMessage mimeMessage = normalJavaMailSender.createMimeMessage();
    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(emailMessage.getToEmail());
      mimeMessageHelper.setSubject(emailMessage.getSubject());
      mimeMessageHelper.setText(emailMessage.getMessage(), true);
      normalJavaMailSender.send(mimeMessage);

      // 시간 측정
      long endTime = System.currentTimeMillis();
      log.info("Execution mail send: {}", endTime - startTime);
    } catch (MessagingException e) {
      throw new RuntimeException("메일 발송 중 에러가 발생했습니다.", e);
    }
  }
}
