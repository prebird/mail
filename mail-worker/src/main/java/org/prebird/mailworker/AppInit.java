package org.prebird.mailworker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AppInit implements CommandLineRunner {

  @Override
  public void run(String... args) throws Exception {
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    log.info("availableProcessors : {}", availableProcessors);
  }
}
