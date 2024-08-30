package org.prebird.shop.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.exception.MailParsingException;
import org.prebird.shop.mail.domain.EmailMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Profile("real")
@Slf4j
@RequiredArgsConstructor
@Service
public class JavaMailService implements MailService{
  private final JavaMailSender javaMailSender;

  /**
   * Java Mail Sender를 사용하여 비동기 방식으로 메일을 발송합니다.
   * <b>주의 : </b> 메일 발송 이후 호출한 트랜잭션이 롤백되어 정합성이 깨지는 문제를 막기 위해, 아래 방법을 사용할 수 있습니다.
   *          <ul>
   *            <li>호출 로직 마지막에 메일 발송 로직이 호출되도록 구현한다.</li>
   *            <li>Spring Event 를 사용하여 커밋 이후에 메일이 발송되도록 구현한다.</li>
   *          </ul>
   * @param emailMessage
   */
  @Retryable(interceptor = "mailRetryInterceptor")
  @Async("mailServiceTaskExecutor")
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
      throw new MailParsingException(e);
    }
  }
}
