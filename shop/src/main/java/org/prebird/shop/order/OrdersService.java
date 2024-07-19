package org.prebird.shop.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.prebird.shop.mail.domain.EmailType;
import org.prebird.shop.product.Product;
import org.prebird.shop.product.ProductRepository;
import org.prebird.shop.mail.domain.EmailMessage;
import org.prebird.shop.mail.service.MailService;
import org.prebird.shop.member.Member;
import org.prebird.shop.member.MemberRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrdersService {
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final OrdersRepository ordersRepository;
  private final MailService mailService;

  @Transactional
  public void order(String username, Long productId) {
    Member member = memberRepository.findByUsername(username).orElseThrow();
    Product product = productRepository.findById(productId).orElseThrow();

    // 주문 완료 메일 발송
    mailService.send(EmailMessage.builder()
            .toEmail(member.getEmail())
            .subject("주문 완료 메일")
            .message("<h1> 주문이 완료 되었습니다. </h1>")
            .emailType(EmailType.NORMAL)
        .build());

    // 주문 데이터 저장
    ordersRepository.save(Orders.builder()
            .buyer(member)
            .price(product.getPrice())
        .build());
  }
}
