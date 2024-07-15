package org.prebird.mailworker.woker;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.prebird.mailworker.domain.EmailMessageRepository;
import org.prebird.mailworker.domain.ErrorLog;
import org.prebird.mailworker.domain.ErrorLogRepository;
import org.prebird.mailworker.service.MailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncMailProcessor {
  private final MailService mailService;
  private final EmailMessageRepository emailMessageRepository;
  private final ErrorLogRepository errorLogRepository;

  @Async
  public CompletableFuture<Void> sendUnprocessedMail(EmailMessage emailMessage) {
    long startTime = System.currentTimeMillis();
    return CompletableFuture.runAsync(() -> {
      try {
        // 메일 발송
        log.info(">> id: {} 메일 발송 호출", emailMessage.getId());
        mailService.send(emailMessage);
        // 발송 완료 처리
        emailMessage.completeSend();
        emailMessageRepository.save(emailMessage);
        long endTime = System.currentTimeMillis();

        log.info("id: {} 메일 발송 처리, 수행시간: {}", emailMessage.getId(), endTime - startTime);
      } catch (Exception e) {
        errorLogRepository.save(new ErrorLog(ErrorLog.getStackTraceAsString(e)));
        log.error("메일 발송 실패: id: {}", emailMessage.getId(), e);
      }
    });
  }
}
