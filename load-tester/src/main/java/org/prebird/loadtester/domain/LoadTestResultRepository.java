package org.prebird.loadtester.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadTestResultRepository extends JpaRepository<LoadTestResult, Long> {
  void deleteByLoadTest_Id(Long loadTestId);
  List<LoadTestResult> findByLoadTestOrderByRequestTime(LoadTest loadTest);
}
