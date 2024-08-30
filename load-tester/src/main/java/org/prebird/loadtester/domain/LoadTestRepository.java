package org.prebird.loadtester.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadTestRepository extends JpaRepository<LoadTest, Long> {
  void deleteById(Long id);
}
