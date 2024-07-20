package org.prebird.mailworker.woker;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.prebird.mailworker.domain.EmailMessageRepository;
import org.prebird.mailworker.domain.EmailStatus;
import org.prebird.mailworker.domain.EmailType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NormalMailWorker extends MailWorker {
  private final EmailMessageRepository emailMessageRepository;

  public NormalMailWorker(EmailMessageRepository emailMessageRepository, AsyncMailProcessor asyncNormalMailProcessor) {
    super(emailMessageRepository, asyncNormalMailProcessor);
    this.emailMessageRepository = emailMessageRepository;
  }

  /**
   * 일반(Normal) 타입, 미처리 상태의 메일을 조회합니다.
   * @return
   */
  @Override
  protected List<EmailMessage> findEmailToProcess() {
    log.info("[NormalMailWorker]: 메일 조회 시작");
    List<EmailMessage> mailToProcess = emailMessageRepository.findByEmailStatusAndEmailType(
        EmailStatus.UNPROCESSED, EmailType.NORMAL);
    log.info("[NormalMailWorker]: 조회한 메일 건수 : {}", mailToProcess.size());
    return mailToProcess;
  }
}
