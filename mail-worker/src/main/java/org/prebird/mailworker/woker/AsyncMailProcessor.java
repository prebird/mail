package org.prebird.mailworker.woker;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.prebird.mailworker.domain.EmailMessageRepository;
import org.prebird.mailworker.service.MailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncMailProcessor {
  private final MailService mailService;
  private final EmailMessageRepository emailMessageRepository;

  @Async
  public CompletableFuture<Void> sendUnprocessedMail(EmailMessage emailMessage) {
    return CompletableFuture.runAsync(() -> {
      try {
        // 메일 발송
        log.info(">> id: {} 메일 발송 호출", emailMessage.getId());
        mailService.send(emailMessage);
        // 발송 완료 처리
        emailMessage.completeSend();
        emailMessageRepository.save(emailMessage);
        log.info("id: {} 메일 발송 처리", emailMessage.getId());
      } catch (Exception e) {
        log.error("메일 발송 실패: id: {}", emailMessage.getId(), e);
      }
    });
  }
}
