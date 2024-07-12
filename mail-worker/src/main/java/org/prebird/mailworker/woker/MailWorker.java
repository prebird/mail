package org.prebird.mailworker.woker;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.prebird.mailworker.domain.EmailMessageRepository;
import org.prebird.mailworker.domain.EmailStatus;
import org.prebird.mailworker.service.MailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailWorker {
  private final EmailMessageRepository emailMessageRepository;
  private final MailService mailService;

  @Scheduled(fixedDelay = 10000)  // 10초에 한번
  public void sendUnProcessedMail() {
    List<EmailMessage> unprocessedMails = emailMessageRepository.findByEmailStatus(EmailStatus.UNPROCESSED);
    log.info("조회한 미처리 메일 갯수: {}", unprocessedMails.size());

    for (EmailMessage unprocessedMail: unprocessedMails) {
      sendUnprocessedMail(unprocessedMail);
    }
  }

  private void sendUnprocessedMail(EmailMessage emailMessage) {
    mailService.send(emailMessage);
    // 발송 완료 처리
    emailMessage.completeSend();
    emailMessageRepository.save(emailMessage);
    log.info("id: {} 메일 발송 처리", emailMessage.getId());
  }
}
