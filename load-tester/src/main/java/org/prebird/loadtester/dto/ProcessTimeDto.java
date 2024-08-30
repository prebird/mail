package org.prebird.loadtester.dto;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.prebird.loadtester.domain.LoadTestResult;

@AllArgsConstructor
@Getter
public class ProcessTimeDto {
  private Long index;
  private Double second;

  public static ProcessTimeDto from(LoadTestResult loadTestResult, Long index) {
    double processTimeInSeconds = (loadTestResult.getFinishTime() == null)
        ? 0d
        : Duration.between(loadTestResult.getRequestTime(), loadTestResult.getFinishTime()).toMillis() / 1000.0;

    return new ProcessTimeDto(index, processTimeInSeconds);
  }
}
