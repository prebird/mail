package org.prebird.shop.config;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
class MailRetryPolicyTest {
  @Test
  void 재시도불가_메세지를_포함하는_예외에_재시도를_하지_않습니다() throws Exception {
    // given
    String message = "No recipient addresses";
    MailRetryPolicy mailRetryPolicy = new MailRetryPolicy(3,
        Collections.singletonMap(MailSendException.class, true));
    RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(mailRetryPolicy);
    final AtomicInteger tryCount = new AtomicInteger();

    // when
    try {
      retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
        log.info("retry count: {}", context.getRetryCount());
        tryCount.incrementAndGet();
        throw new MailSendException(message);
      });
    } catch (RuntimeException e) {
      log.info("재시도 종료");
    }

    // then
    assertThat(tryCount.intValue()).isEqualTo(1);
  }

  @Test
  void 재시도불가_메세지를_포함하지_않는_예외에_재시도를_3회_실시합니다() throws Exception {
    // given
    String message = "재시도 가능 에러 메세지";
    MailRetryPolicy mailRetryPolicy = new MailRetryPolicy(3,
        Collections.singletonMap(MailSendException.class, true));
    RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(mailRetryPolicy);
    final AtomicInteger tryCount = new AtomicInteger();

    // when
    try {
      retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
        log.info("retry count: {}", context.getRetryCount());
        tryCount.incrementAndGet();
        throw new MailSendException(message);
      });
    } catch (RuntimeException e) {
      log.info("재시도 종료");
    }

    // then
    assertThat(tryCount.intValue()).isEqualTo(3);
  }
}
