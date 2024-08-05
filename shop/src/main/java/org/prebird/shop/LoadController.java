package org.prebird.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prebird.shop.member.MemberService;
import org.prebird.shop.order.OrdersService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LoadController {
  private final OrdersService ordersService;
  private final MemberService memberService;

  /**
   * 일반 메일 부하를 생성합니다.
   * @param vUsers  vUsers 수 (동시 요청자 수)
   * @param interval 요청 간격
   * @param repeat 동시 요청 반복 수
   * @throws InterruptedException
   */
  @PostMapping("/generate-load/mail/normal")
  public void generateNormalMailLoad(@RequestParam int vUsers, @RequestParam int interval, @RequestParam int repeat) throws InterruptedException {
    for (int i = 0; i < repeat; i++) {
      generateAsyncLoad(vUsers, () -> ordersService.order("tester01", 1L));
      Thread.sleep(interval);
    }
  }

  /**
   * 긴급 메일 부하를 생성합니다.
   * @param vUsers vUsers 수 (동시 요청자 수)
   * @param interval 요청 간격
   * @param repeat 동시 요청 반복 수
   * @throws InterruptedException
   */
  @PostMapping("/generate-load/mail/urgent")
  public void generateUrgentMailLoad(@RequestParam int vUsers, @RequestParam int interval, @RequestParam int repeat) throws InterruptedException {
    for (int i = 0; i < repeat; i++) {
      generateAsyncLoad(vUsers, () -> memberService.sendVerifyMail("iopengom@naver.com")) ;
      Thread.sleep(interval);
    }
  }

  /**
   * vusers 만큼 동시에 method를 호출합니다.
   * @param vUsers
   * @param method
   * @return
   */
  public CompletableFuture<Void> generateAsyncLoad(int vUsers, Runnable method) {
    log.info("generate {} load", vUsers);
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    // vsuers 만큼 동시에 메서드를 호출함
    for (int i = 0; i < vUsers; i++) {
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        method.run();
      });
      futures.add(future);
    }

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
  }
}
