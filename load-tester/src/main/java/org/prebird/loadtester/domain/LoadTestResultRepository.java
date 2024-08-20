package org.prebird.loadtester.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadTestResultRepository extends JpaRepository<LoadTestResult, Long> {

}
