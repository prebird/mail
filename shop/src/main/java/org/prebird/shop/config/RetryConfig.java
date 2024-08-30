package org.prebird.shop.config;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSendException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

  @Value("${mail.retry.count}")
  private Integer mailRetryCount;

  @Bean
  public RetryTemplate mailRetryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    MailRetryPolicy mailRetryPolicy = new MailRetryPolicy(mailRetryCount,
        Collections.singletonMap(MailSendException.class, true));
    retryTemplate.setRetryPolicy(mailRetryPolicy);
    retryTemplate.setBackOffPolicy(new FixedBackOffPolicy()); // 1초 대기

    return retryTemplate;
  }

  @Bean
  public RetryOperationsInterceptor mailRetryInterceptor() {
    return RetryInterceptorBuilder.stateless()
        .retryOperations(mailRetryTemplate())
        .build();
  }
}
