package org.prebird.shop.config;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSendException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

  @Bean
  public RetryTemplate mailRetryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    MailRetryPolicy mailRetryPolicy = new MailRetryPolicy(3,
        Collections.singletonMap(MailSendException.class, true));
    retryTemplate.setRetryPolicy(mailRetryPolicy);

    return retryTemplate;
  }

  @Bean
  public RetryOperationsInterceptor mailRetryInterceptor() {
    return RetryInterceptorBuilder.stateless()
        .retryOperations(mailRetryTemplate())
        .build();
  }
}
