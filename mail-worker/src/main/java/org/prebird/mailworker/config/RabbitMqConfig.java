package org.prebird.mailworker.config;

import java.time.Duration;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;

@Profile({"message-queue-console"})
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

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(CachingConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setPrefetchCount(5); // 프리페치 크기 설정
    factory.setMessageConverter(messageConverter());
    factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
    factory.setConcurrentConsumers(32); // 동시성 설정
    factory.setMaxConcurrentConsumers(32); // 최대 동시성 설정
    factory.setDefaultRequeueRejected(false);

    factory.setAdviceChain(RetryInterceptorBuilder.stateless()
        .maxAttempts(3)
        .backOffOptions(Duration.ofSeconds(3L).toMillis(), 2, Duration.ofSeconds(10L).toMillis())
        .build());

    return factory;
  }
}
