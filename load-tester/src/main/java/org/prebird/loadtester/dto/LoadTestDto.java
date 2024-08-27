package org.prebird.loadtester.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.prebird.loadtester.domain.LoadTest;

@AllArgsConstructor @Builder
@Getter
public class LoadTestDto {
  private Long id;
  private String description;
  private String testParams;
  private LocalDateTime startTime;

  public static LoadTestDto from(LoadTest loadTest) {
    return LoadTestDto.builder()
        .id(loadTest.getId())
        .description(loadTest.getDescription())
        .testParams(loadTest.getTestParams())
        .startTime(loadTest.getStartTime())
        .build();
  }
}
