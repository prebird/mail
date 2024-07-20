package org.prebird.shop;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.prebird.shop.member.MemberService;
import org.prebird.shop.order.OrdersService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {

  private final OrdersService ordersService;
  private final MemberService memberService;

  @PostMapping("/make-stress")
  public void makeStress(@RequestParam Integer repeat) {
    IntStream.range(0, repeat/2).forEach(i -> {
      ordersService.order("tester01", 1L);
      memberService.sendVerifyMail("iopengom@naver.com");
      });
  }
}
