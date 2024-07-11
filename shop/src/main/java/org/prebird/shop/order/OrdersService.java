package org.prebird.shop.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.prebird.shop.Product.Product;
import org.prebird.shop.Product.ProductRepository;
import org.prebird.shop.member.Member;
import org.prebird.shop.member.MemberRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrdersService {
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final OrdersRepository ordersRepository;


  @Transactional
  public void order(String username, Long productId) {
    Member member = memberRepository.findByUsername(username).orElseThrow();
    Product product = productRepository.findById(productId).orElseThrow();

    ordersRepository.save(Orders.builder()
            .buyer(member)
            .price(product.getPrice())
        .build());
  }

}
