package org.prebird.shop.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.mail.domain.EmailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Profile("mock-mail")
@Service
public class MockServerMailService implements MailService {
  @Value("${mock-mail.server-url}")
  private String emailServerUrl;
  @Value(("${mock-mail.account}"))
  private String emailAccount;

  /**
   * 이메일 mock 서버에 발송요청을 보냅니다.
   * @param emailMessage 메일 객체
   */
  @Override
  public void send(EmailMessage emailMessage) {
    requestProcessMail(emailMessage);
  }

  private void requestProcessMail(EmailMessage emailMessage) {
    RestClient restClient = RestClient.builder()
        .baseUrl(emailServerUrl).build();

    restClient.post()
        .uri(uriBuilder -> uriBuilder
            .path("/process-mail")
            .queryParam("account", emailAccount)
            .build())
        .retrieve()
        .onStatus(status -> status.equals(HttpStatus.TOO_MANY_REQUESTS), (request, response) -> {
          throw  new MailSendException("잠시 후 다시 시도해 주세요");
        });
  }
}
