package org.prebird.mailworker.mailWorker.DatabaseMailWorker;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.prebird.mailworker.domain.EmailMessageRepository;
import org.prebird.mailworker.domain.EmailStatus;
import org.prebird.mailworker.domain.EmailType;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * 긴급(urgent) 타입의 메일을 주기적으로 조회하여 메일을 발송합니다.
 */
@Slf4j
@Component
public class UrgentDBMailWorker extends DBMailWorker {
  private final EmailMessageRepository emailMessageRepository;
  private static final int BATCH_COUNT = 20;

  public UrgentDBMailWorker(EmailMessageRepository emailMessageRepository, DBAsyncMailProcessor asyncUrgentMailProcessor) {
    super(emailMessageRepository, asyncUrgentMailProcessor);
    this.emailMessageRepository = emailMessageRepository;
  }

  /**
   * 긴급(Urgent) 타입, 미처리 상태의 메일을 조회합니다.
   * @return
   */
  @Override
  protected List<EmailMessage> findEmailToProcess() {
    log.info("[UrgentMailWorker]: 메일 조회 시작");
    List<EmailMessage> mailToProcess = emailMessageRepository.findByEmailStatusAndEmailType(
        EmailStatus.UNPROCESSED, EmailType.URGENT, PageRequest.of(0, BATCH_COUNT));
    log.info("[UrgentMailWorker]: 조회한 메일 건수 : {}", mailToProcess.size());
    return mailToProcess;
  }
}
