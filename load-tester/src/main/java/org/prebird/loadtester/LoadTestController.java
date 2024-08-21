package org.prebird.loadtester;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.loadtester.domain.LoadTest;
import org.prebird.loadtester.domain.LoadTestRepository;
import org.prebird.loadtester.domain.LoadTestResult;
import org.prebird.loadtester.domain.LoadTestResultRepository;
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

  /**
   * 부하테스트 결과를 요약합니다.
   * @param id
   * @return
   */
  @GetMapping("/load-tests/{id}/summary")
  public List<LoadTestResultSummaryDto> getLoadTestResult(@PathVariable Long id) {
    return loadTestRepository.findWithResultById(id)
        .map(this::summaryResult)
        .orElseGet(Collections::emptyList);
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
      });
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
