package org.prebird.shop.config;

import java.util.List;
import java.util.Map;
import org.springframework.mail.MailSendException;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;

public class MailRetryPolicy extends SimpleRetryPolicy {

  public MailRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
    super(maxAttempts, retryableExceptions);
  }

  // 재시도 불가 메세지
  private static List<String> nonRetryableMessages = List.of(
      "Failed to close server connection after message sending" // 메세지 발송 후 에러난 경우
      , "SMTP can only send RFC822 messages"    // 양식이 잘못된 경우
      , "is not an InternetAddress" // 메일 주소가 잘못된 경우
      , "No recipient addresses" // 없는 수신인인 경우
  );

  @Override
  public boolean canRetry(RetryContext context) {
    Throwable throwable = context.getLastThrowable();
    if ((throwable instanceof MailSendException) && containsNonRetryableMessage(throwable)) {
      return false; // 재시도 불가 메세지를 가진 예외이면 재시도 하지 않음
    }
    return super.canRetry(context); // 기존의 재시도 로직
  }

  /**
   * 재시도 불가 메세지가 예외에 포함되어 있는지
   * @param throwable
   * @return
   */
  private boolean containsNonRetryableMessage(Throwable throwable) {
    for (String nonRetryableMessage: nonRetryableMessages) {
      if (throwable.getMessage().contains(nonRetryableMessage)) {
        return true;
      }
    }
    return false;
  }
}
