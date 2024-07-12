package org.prebird.shop.order;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrdersController {
  private final OrdersService ordersService;

  /**
   * 상품 주문
   * @param username
   * @param productId
   */
  @PostMapping("/orders")
  public void order(@RequestParam String username, @RequestParam Long productId) {
    ordersService.order(username, productId);
  }

  /**
   *  repeat 건수 반복하여 주문합니다.
   */
  @PostMapping("/orders/repeat")
  public void orderRepeatedly(@RequestParam Integer repeat, @RequestParam String username, @RequestParam Long productId) {
    long startTime = System.currentTimeMillis();
    IntStream.range(0, repeat).forEach(
        i -> ordersService.order(username, productId)
    );
    long endTime = System.currentTimeMillis();
    log.info(">>> 총 소요 시간: {}", endTime - startTime);
  }
}
