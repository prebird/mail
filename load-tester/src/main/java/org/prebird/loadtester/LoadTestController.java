package org.prebird.loadtester;

import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.loadtester.domain.LoadTest;
import org.prebird.loadtester.domain.LoadTestRepository;
import org.prebird.loadtester.domain.LoadTestResult;
import org.prebird.loadtester.domain.LoadTestResultRepository;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LoadTestController {
  private final LoadTestRepository loadTestRepository;
  private final LoadTestResultRepository loadTestResultRepository;
  private final TaskExecutor loadTestTaskExecutor;

  /**
   * 일정 수준의 부하를 발생시킵니다.
   * @param vUsers        동시사용자수
   * @param interval      요청 간격
   * @param loop          반복수
   * @param description   테스트 설명
   */
  @PostMapping("/load-tests/generate")
  public void generateLoad(@RequestParam int vUsers, @RequestParam long interval,
      @RequestParam int loop, @RequestParam String description) {
    // 테스트 정보 저장
    LoadTest loadTest = loadTestRepository.save(LoadTest.builder()
        .testParams(String.format("vUsers=%d/interval=%d/loop=%d", vUsers, interval, loop))
        .startTime(LocalDateTime.now())
        .description(description)
        .build());
    // 부하 발생
    for (int loopIdx = 0; loopIdx < loop; loopIdx++) {
      generateVUsersLoadAsync(vUsers, loadTest, loopIdx);
      sleep(interval);
    }
  }

  @GetMapping("/load-tests/{id}/tps")
  public List<TpsDto> getLoadTestTps(@PathVariable Long id) {
    LoadTest loadTest = getLoadTest(id);
    List<LoadTestResult> results = loadTestResultRepository.findByLoadTestOrderByRequestTime(loadTest);

    Map<Long, Double> tps = new TreeMap<>();
    for (LoadTestResult result : results) {
      if (result.getFinishTime() == null) { // finish_time이 null인 경우 건너뜁니다.
        continue;
      }
      long elapsedTime = Duration.between(results.get(0).getRequestTime(), result.getRequestTime())
          .getSeconds();
      long processingTime = Duration.between(result.getRequestTime(), result.getFinishTime())
          .toMillis();

      // TPS 계산
      tps.merge(elapsedTime, 1.0 / processingTime, Double::sum);
    }
    return tps.entrySet().stream()
        .map(entry -> new TpsDto(entry.getKey(), entry.getValue() * 1000))
        .collect(Collectors.toList());
  }

  private LoadTest getLoadTest(Long id) {
    return loadTestRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("결과가 없습니다."));
  }

  @GetMapping("/load-tests/{id}/process-time")
  public List<ProcessTimeDto> getLoadTestProcessTime(@PathVariable Long id) {
    LoadTest loadTest = getLoadTest(id);
    List<LoadTestResult> results = loadTestResultRepository.findByLoadTestOrderByRequestTime(loadTest);
    AtomicLong index = new AtomicLong(0);
    return results.stream().map(result -> ProcessTimeDto.from(result, index.getAndIncrement()))
        .collect(Collectors.toList());
  }

  /**
   * 부하테스트 결과 삭제
   * @param id
   */
  @Transactional
  @DeleteMapping("/load-tests/{id}")
  public void deleteLoadTest(@PathVariable Long id) {
    loadTestResultRepository.deleteByLoadTest_Id(id);
    loadTestRepository.deleteById(id);
  }


  List<LoadTestResultSummaryDto> summaryResult(LoadTest loadTest) {
    Map<Integer, List<LoadTestResult>> groupedByLoopIdx = loadTest.getLoadTestResults().stream()
        .collect(Collectors.groupingBy(LoadTestResult::getLoopIdx));

    return groupedByLoopIdx.entrySet().stream()
        .map(entry -> LoadTestResultSummaryDto.from(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }


  private CompletableFuture<Void> generateVUsersLoadAsync(int vUsers, LoadTest loadTest, int loopIndex) {
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    for (int i = 0; i < vUsers; i++) {
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        // 요청 시간 저장
        LoadTestResult loadTestResult = loadTestResultRepository.save(
            loadTestResultRepository.save(LoadTestResult.builder()
                .loadTest(loadTest)
                .loopIdx(loopIndex)
                .requestTime(LocalDateTime.now())
                .build()));
        // 메일 발송 요청
        requestSendMail(loadTestResult.getId());
      }, loadTestTaskExecutor);
      futures.add(future);
    }

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
  }

  private void requestSendMail(Long loadTestResultId) {
    RestClient restClient = RestClient.builder()
        .baseUrl("http://localhost:8081").build();
    try {
      restClient.post()
          .uri(uriBuilder -> uriBuilder
              .path("/load-test/send-mail")
              .queryParam("loadTestResultId", loadTestResultId)
              .build())
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientResponseException ex) {
      log.error("[error] {}", ex);
    }
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      log.error("sleep {}", e);
    }
  }
}
