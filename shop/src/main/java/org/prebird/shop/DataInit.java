package org.prebird.shop;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.product.Product;
import org.prebird.shop.product.ProductRepository;
import org.prebird.shop.member.Member;
import org.prebird.shop.member.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInit implements CommandLineRunner {
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final DataSource dataSource;
  private final Environment env;

  @Override
  public void run(String... args) throws Exception {
    truncate();

    memberRepository.saveAndFlush(Member.builder()
            .username("tester01")
            .email("iopengom@naver.com")
        .build());

    productRepository.saveAndFlush(Product.builder()
            .id(1L)
            .name("사과")
            .price(1000)
        .build());

    logAppLog();
    logMemorySettings();
  }

  private void truncate() {
    Resource resource = new ClassPathResource("truncate.sql");
    try (Connection connection = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(connection, resource);
      System.out.println("Executed truncate.sql");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to execute truncate.sql", e);
    }
  }

  private void logAppLog() {
    String mailRetryCount = env.getProperty("mail.retry.count");
    log.info("----- app log -------");
    log.info("mailRetryCount : {}", mailRetryCount);
  }

  public static void logMemorySettings() {
    long xms = Runtime.getRuntime().totalMemory(); // 초기 힙 메모리 (Xms)
    long xmx = Runtime.getRuntime().maxMemory(); // 최대 힙 메모리 (Xmx)

    log.info("Initial Heap Size (Xms): " + xms / (1024 * 1024) + " MB");
    log.info("Max Heap Size (Xmx): " + xmx / (1024 * 1024) + " MB");
  }
}
