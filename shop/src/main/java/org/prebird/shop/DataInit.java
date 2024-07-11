package org.prebird.shop;

import lombok.RequiredArgsConstructor;
import org.prebird.shop.Product.Product;
import org.prebird.shop.Product.ProductRepository;
import org.prebird.shop.member.Member;
import org.prebird.shop.member.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataInit implements CommandLineRunner {
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;

  @Override
  public void run(String... args) throws Exception {
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
}
