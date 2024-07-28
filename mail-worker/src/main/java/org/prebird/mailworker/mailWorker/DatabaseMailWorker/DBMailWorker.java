package org.prebird.mailworker.mailWorker.DatabaseMailWorker;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.mailworker.domain.EmailMessage;
import org.prebird.mailworker.domain.EmailMessageRepository;
import org.prebird.mailworker.domain.EmailStatus;
import org.prebird.mailworker.mailWorker.MailWorker;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class DBMailWorker implements MailWorker {
  private final EmailMessageRepository emailMessageRepository;
  private final DBAsyncMailProcessor asyncMailProcessor;
  private static final int BATCH_COUNT = 20;  // 한번에 처리할 메일 갯수

  @Override
  @Scheduled(fixedDelay = 1000)  // 이전 작업이 끝난 후, 1초 이후 수행됨 -> 중복 수행 방지됨
  public void sendUnProcessedMail() {
    List<EmailMessage> unprocessedMails = fetchEmailToProcess();
    log.info("조회한 미처리 메일 갯수: {}", unprocessedMails.size());

    // 미안료 메일 비동기적으로 처리
    List<CompletableFuture<Void>> futures = unprocessedMails.stream()
        .map(mail -> asyncMailProcessor.sendUnprocessedMail(mail))
        .collect(Collectors.toList());

    // 모든 비동기 작업 완료 된 후 종료
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
  }

  /**
   * 처리할 메일을 조회합니다.
   * @return
   */
  @Override
  public List<EmailMessage> fetchEmailToProcess() {
    return emailMessageRepository.findByEmailStatus(EmailStatus.UNPROCESSED, PageRequest.of(0, BATCH_COUNT));
  }
}
