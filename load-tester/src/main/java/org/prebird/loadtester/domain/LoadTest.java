package org.prebird.loadtester.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.Async;

@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class LoadTest {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String description;

  private String testParams;

  private LocalDateTime startTime;

  @OneToMany(mappedBy = "loadTest")
  private List<LoadTestResult> loadTestResults;
}
