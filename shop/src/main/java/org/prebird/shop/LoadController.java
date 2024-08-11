package org.prebird.shop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.load.LoadTest;
import org.prebird.shop.load.LoadTestRepository;
import org.prebird.shop.load.SaveLoadTestRequest;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.domain.EmailMessageRepository;
import org.prebird.shop.mail.domain.EmailStatus;
import org.prebird.shop.member.MemberService;
import org.prebird.shop.order.OrdersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LoadController {
  private final OrdersService ordersService;
  private final MemberService memberService;
  private final EmailMessageRepository emailMessageRepository;
  private final LoadTestRepository loadTestRepository;

  /**
   * 일반 메일 부하를 생성합니다.
   * @param vUsers  vUsers 수 (동시 요청자 수)
   * @param interval 요청 간격
   * @param duration 테스트 지속 시간
   * @throws InterruptedException
   */
  @PostMapping("/generate-load/mail/normal")
  public void generateNormalMailLoad(@RequestParam int vUsers, @RequestParam int interval, @RequestParam int duration) throws InterruptedException {
    LocalDateTime finishTime = LocalDateTime.now().plusSeconds(duration);

    while (LocalDateTime.now().isBefore(finishTime)) {
      generateAsyncLoad(vUsers, () -> ordersService.order("tester01", 1L));
      Thread.sleep(interval * 1000);
    }
  }

  /**
   * 긴급 메일 부하를 생성합니다.
   * @param vUsers vUsers 수 (동시 요청자 수)
   * @param interval 요청 간격
   * @param repeat 동시 요청 반복 수
   * @throws InterruptedException
   */
  @PostMapping("/generate-load/mail/urgent")
  public void generateUrgentMailLoad(@RequestParam int vUsers, @RequestParam int interval, @RequestParam int repeat) throws InterruptedException {
    for (int i = 0; i < repeat; i++) {
      generateAsyncLoad(vUsers, () -> memberService.sendVerifyMail("iopengom@naver.com")) ;
      Thread.sleep(interval);
    }
  }

  /**
   * vusers 만큼 동시에 method를 호출합니다.
   * @param vUsers
   * @param method
   * @return
   */
  public CompletableFuture<Void> generateAsyncLoad(int vUsers, Runnable method) {
    log.info("generate {} load", vUsers);
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    // vsuers 만큼 동시에 메서드를 호출함
    for (int i = 0; i < vUsers; i++) {
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        method.run();
      });
      futures.add(future);
    }

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
  }

  /**
   * 부하 테스트 결과를 계산합니다.
   */
  @GetMapping("/calculate-load-test-result")
  public LoadTest calculateLoad() {

    List<EmailMessage> emailMessages = emailMessageRepository.findAll();

    long processCount = emailMessages.stream().filter(emailMessage -> emailMessage.getEmailStatus() == EmailStatus.PROCESSED).count();
    long failCount = emailMessages.stream().filter(emailMessage -> emailMessage.getEmailStatus() == EmailStatus.UNPROCESSED).count();
    LocalDateTime firstRequestAt = emailMessages.stream().map(EmailMessage::getRequestAt).min(LocalDateTime::compareTo).get();
    LocalDateTime lastSentAt = emailMessages.stream().map(EmailMessage::getSentAt).filter(sentAt -> sentAt != null).max(LocalDateTime::compareTo).get();
    double duration = (double) Duration.between(firstRequestAt, lastSentAt).toMillis() / 1000.0;
    double tps = processCount / duration;
    double averageTime = emailMessages.stream().filter(em -> em.getSentAt() != null).mapToDouble(em -> Duration.between(em.getRequestAt(), em.getSentAt()).toMillis() / 1000.0).average().getAsDouble();
    double maxTime = emailMessages.stream().filter(em -> em.getSentAt() != null).mapToDouble(em -> Duration.between(em.getRequestAt(), em.getSentAt()).toMillis() / 1000.0).max().getAsDouble();

    log.info("처리건수: {} / 미처리 건수: {}", processCount, failCount);
    log.info("걸린 시간: {} - {} = {}", firstRequestAt, lastSentAt, duration);
    log.info("TPS: {}", tps);
    log.info("평균 처리시간: {}", averageTime); // AVG(전송 시각 - 요청 시각)
    log.info("최대 처리시간: {}", averageTime); // MAX(전송 시각 - 요청 시각)

    return LoadTest.builder()
        .successCount(processCount)
        .failCount(failCount)
        .firstRequestAt(firstRequestAt)
        .lastSentAt(lastSentAt)
        .duration(duration)
        .tps(tps)
        .averageTimePerMail(averageTime)
        .maxTimePerMail(maxTime)
        .build();
  }

  @PostMapping("/save-load-test-result")
  public void saveLoadTestResult(@RequestBody SaveLoadTestRequest request) {
    LoadTest loadTest = calculateLoad();
    loadTest.setDescription(request.getDescription());
    loadTestRepository.save(loadTest);
  }

  @PostMapping("/clear-load")
  public void clearLoadResult() {
    log.info("clear all result");
    emailMessageRepository.deleteAll();
  }
}
