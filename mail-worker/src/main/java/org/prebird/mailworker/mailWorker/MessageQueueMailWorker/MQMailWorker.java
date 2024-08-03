package org.prebird.mailworker.mailWorker.MessageQueueMailWorker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.prebird.mailworker.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("message-queue-console")
@Slf4j
@RequiredArgsConstructor
@Component
public class MQMailWorker {
  private final MailService mailService;
  public static final String NORMAL_MAIL_QUEUE = "normal.mail.queue";


  /**
   * 동기방식 메세지 소비 Listener
   * @param emailMessage
   */
  @RabbitListener(queues = {NORMAL_MAIL_QUEUE})
  public void fetchMessageAndSendMailSync(EmailMessage emailMessage) {
    log.info("--------------------------------");
    log.info("fetchMessageAndSendMailSync called");
    mailService.send(emailMessage);
  }
}
