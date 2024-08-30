package org.prebird.shop.loadTest;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LoadTestResultRepository extends JpaRepository<LoadTestResult, Long> {
  @Modifying
  @Transactional
  @Query("UPDATE LoadTestResult ltr SET ltr.finishTime = :finishTime WHERE ltr.id = :loadTestResultId")
  void updateFinishTime(Long loadTestResultId, LocalDateTime finishTime);
}
