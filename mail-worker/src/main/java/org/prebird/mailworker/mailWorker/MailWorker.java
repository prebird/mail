package org.prebird.mailworker.mailWorker;

import java.util.List;
import org.prebird.mailworker.domain.EmailMessage;

public interface MailWorker {

  /**
   * 미처리된 메일을 발송합니다.
   */
  void sendUnProcessedMail();

  /**
   * 처리할 메일을 가져옵니다.
   * @return
   */
  List<EmailMessage> fetchEmailToProcess();
}
