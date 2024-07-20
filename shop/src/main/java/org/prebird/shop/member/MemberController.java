package org.prebird.shop.member;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
  private final MemberService memberService;


  /**
   * 본인 인증 메일 발송
   */
  @PostMapping("/send-verify-mail")
  public void sendVerifyMail(@RequestParam String email) {
    memberService.sendVerifyMail(email);
  }

  /**
   * 본인 인증 메일 발송 (반복)
   */
  @PostMapping("/send-verify-mail/repeat")
  public void sendVerifyMail(@RequestParam Integer repeat, @RequestParam String email) {
    IntStream.range(0, repeat).forEach(
        i -> sendVerifyMail(email)
    );
  }
}
