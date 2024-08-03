package org.prebird.shop.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeadLetterConfig {
  public static final String DEAD_LETTER_EXCHANGE = "dead-letter-exchange";
  public static final String DEAD_LETTER_QUEUE_MESSAGE = "dead-letter-queue-message";
  public static final String DEAD_LETTER_ROUTING_KEY_MESSAGE = "dead-letter-routing-key-message";

  @Bean
  public Exchange deadLetterExchange() {
    return ExchangeBuilder
        .topicExchange(DEAD_LETTER_EXCHANGE)
        .build();
  }

  @Bean
  public Queue messageDeadLetterQueue() {
    return QueueBuilder.durable(DEAD_LETTER_QUEUE_MESSAGE)
        .build();
  }

  @Bean
  public Binding messageDeadLetterBinding() {
    return BindingBuilder
        .bind(messageDeadLetterQueue())
        .to(deadLetterExchange())
        .with(DEAD_LETTER_ROUTING_KEY_MESSAGE)
        .noargs();
  }
}
