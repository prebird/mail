package org.prebird.shop.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
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
  // dead letter queue


  @Bean
  public TopicExchange mailTopicExchange() {
    return new TopicExchange(MAIL_TOPIC_EXCHANGE_NAME, true, false);
  }

  @Bean
  public Queue urgentMailQueue() {
    return QueueBuilder.durable(URGENT_MAIL_QUEUE)
        .withArgument("x-dead-letter-exchange", DeadLetterConfig.DEAD_LETTER_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", DeadLetterConfig.DEAD_LETTER_ROUTING_KEY_MESSAGE)
        .build();
  }

  @Bean
  public Queue normalMailQueue() {
    return QueueBuilder.durable(NORMAL_MAIL_QUEUE)
        .withArgument("x-dead-letter-exchange", DeadLetterConfig.DEAD_LETTER_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", DeadLetterConfig.DEAD_LETTER_ROUTING_KEY_MESSAGE)
        .build();
  }

  @Bean
  public Binding urgentMailBinding(Queue urgentMailQueue, Exchange mailTopicExchange) {
    return BindingBuilder
        .bind(urgentMailQueue)
        .to(mailTopicExchange)
        .with(URGENT_MAIL_ROUTING_KEY)
        .noargs();
  }

  @Bean
  public Binding normalMailBinding(Queue normalMailQueue, Exchange mailTopicExchange) {
    return BindingBuilder
        .bind(normalMailQueue)
        .to(mailTopicExchange)
        .with(NORMAL_MAIL_ROUTING_KEY)
        .noargs();
  }

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
