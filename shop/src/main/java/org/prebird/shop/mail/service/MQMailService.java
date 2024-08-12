package org.prebird.shop.mail.service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.config.RabbitMqConfig;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.domain.EmailMessageRepository;
import org.prebird.shop.mail.domain.EmailType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 메세지 큐에 메일 발송 내용을 전송하는 서비스입니다.
 */
@Profile("message-queue")
@Slf4j
@RequiredArgsConstructor
@Service
public class MQMailService implements MailService {
  private final RabbitTemplate rabbitTemplate;
  private final EmailMessageRepository emailMessageRepository;

  @Override
  public void send(EmailMessage emailMessage) {
    log.info("MQMailService called");
    EmailMessage savedEmailMessage = emailMessageRepository.save(emailMessage);

//    if (emailType == EmailType.NORMAL) {
//      rabbitTemplate.convertAndSend(RabbitMqConfig.MAIL_TOPIC_EXCHANGE_NAME, RabbitMqConfig.NORMAL_MAIL_ROUTING_KEY, savedEmailMessage);
//      return;
//    }
//    if (emailType == EmailType.URGENT) {
//      rabbitTemplate.convertAndSend(RabbitMqConfig.MAIL_TOPIC_EXCHANGE_NAME, RabbitMqConfig.URGENT_MAIL_ROUTING_KEY, savedEmailMessage);
//      return;
//    }
    throw new IllegalStateException("올바르지 않은 EmailType 입니다.");
  }
}
