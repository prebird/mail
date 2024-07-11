package org.prebird.shop.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
