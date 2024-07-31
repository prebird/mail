package org.prebird.shop.mail.service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.config.RabbitMqConfig;
import org.prebird.shop.mail.domain.EmailMessage;
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
  private AtomicLong sequence = new AtomicLong(0);

  @Override
  public void send(EmailMessage emailMessage, EmailType emailType) {
    log.info("MQMailService called");
    setSequence(emailMessage);
    if (emailType == EmailType.NORMAL) {
      rabbitTemplate.convertAndSend(RabbitMqConfig.MAIL_TOPIC_EXCHANGE_NAME, RabbitMqConfig.NORMAL_MAIL_ROUTING_KEY, emailMessage);
      return;
    }
    if (emailType == EmailType.URGENT) {
      rabbitTemplate.convertAndSend(RabbitMqConfig.MAIL_TOPIC_EXCHANGE_NAME, RabbitMqConfig.URGENT_MAIL_ROUTING_KEY, emailMessage);
      return;
    }
    throw new IllegalStateException("올바르지 않은 EmailType 입니다.");
  }

  // 식별을 위한 sequence 추가
  private void setSequence(EmailMessage emailMessage) {
      emailMessage.setSequenceAsId(sequence.incrementAndGet());
  }
}
