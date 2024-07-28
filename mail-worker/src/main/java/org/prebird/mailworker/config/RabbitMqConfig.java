package org.prebird.mailworker.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("message-queue")
@Configuration
public class RabbitMqConfig {
  // exchange
  public static final String MAIL_TOPIC_EXCHANGE_NAME = "mail.topic";
  // queue
  public static final String URGENT_MAIL_QUEUE = "urgent.mail.queue";
  public static final String NORMAL_MAIL_QUEUE = "normal.mail.queue";
  // routing key
  public static final String URGENT_MAIL_ROUTING_KEY = "urgent.new.mail";
  public static final String NORMAL_MAIL_ROUTING_KEY = "normal.new.mail";

  /**
   * Json 메세지 컨버터
   * @return
   */
  @Bean
  public MessageConverter messageConverter() {
    Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
    return jackson2JsonMessageConverter;
  }
}
