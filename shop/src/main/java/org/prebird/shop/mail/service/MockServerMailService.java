package org.prebird.shop.mail.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.loadTest.LoadTestResultRepository;
import org.prebird.shop.mail.domain.EmailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.mail.MailSendException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MockServerMailService {
  @Value("${mock-mail.server-url}")
  private String emailServerUrl;
  @Value(("${mock-mail.account}"))
  private String emailAccount;

  private final LoadTestResultRepository loadTestResultRepository;

  /**
   * 이메일 mock 서버에 발송요청을 보냅니다.
   *
   * @param emailMessage 메일 객체
   */
  @Retryable(interceptor = "mailRetryInterceptor")
  @Async("mailServiceTaskExecutor")
  @Transactional(propagation = Propagation.NOT_SUPPORTED) // 트랜잭션 적용하지 않음
  public void send(EmailMessage emailMessage, Long loadTestResultId) {
    // 메일 발송 요청
    requestProcessMail(emailMessage);
    //발송 성공 처리 DB 저장
    loadTestResultRepository.updateFinishTime(loadTestResultId, LocalDateTime.now());
  }

  private void requestProcessMail(EmailMessage emailMessage) {
    RestClient restClient = RestClient.builder()
        .baseUrl(emailServerUrl).build();
    try {
      restClient.post()
          .uri(uriBuilder -> uriBuilder
              .path("/mock-process-mail")
              .queryParam("account", emailAccount)
              .build())
          .retrieve()
          .toBodilessEntity();
      log.info("[id: {}] request success", emailMessage.getId());
    } catch (RestClientResponseException ex) {
      log.error("[id: {}] request fail", emailMessage.getId());
      HttpStatusCode statusCode = ex.getStatusCode();
      if (statusCode.equals(HttpStatus.TOO_MANY_REQUESTS)) {
        throw new MailSendException("잠시 후 다시 시도해 주세요");   // Retry 설정된 예외로 변환
      }
    }
  }
}
