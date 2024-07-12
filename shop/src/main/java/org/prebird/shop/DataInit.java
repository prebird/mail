package org.prebird.shop;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.prebird.shop.product.Product;
import org.prebird.shop.product.ProductRepository;
import org.prebird.shop.member.Member;
import org.prebird.shop.member.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataInit implements CommandLineRunner {
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final DataSource dataSource;

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
}
