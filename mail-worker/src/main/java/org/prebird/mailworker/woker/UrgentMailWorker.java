package org.prebird.mailworker.woker;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.prebird.mailworker.domain.EmailMessageRepository;
import org.prebird.mailworker.domain.EmailStatus;
import org.prebird.mailworker.domain.EmailType;
import org.springframework.stereotype.Component;

/**
 * 긴급(urgent) 타입의 메일을 주기적으로 조회하여 메일을 발송합니다.
 */
@Slf4j
@Component
public class UrgentMailWorker extends MailWorker {
  private final EmailMessageRepository emailMessageRepository;

  public UrgentMailWorker(EmailMessageRepository emailMessageRepository, AsyncMailProcessor asyncUrgentMailProcessor) {
    super(emailMessageRepository, asyncUrgentMailProcessor);
    this.emailMessageRepository = emailMessageRepository;
  }

  /**
   * 긴급(Urgent) 타입, 미처리 상태의 메일을 조회합니다.
   * @return
   */
  @Override
  protected List<EmailMessage> findEmailToProcess() {
    List<EmailMessage> mailToProcess = emailMessageRepository.findByEmailStatusAndEmailType(
        EmailStatus.UNPROCESSED, EmailType.URGENT);
    log.info("[UrgentMailWorker]: 조회한 메일 건수 : {}", mailToProcess.size());
    return mailToProcess;
  }
}
