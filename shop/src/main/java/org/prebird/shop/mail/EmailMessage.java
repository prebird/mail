package org.prebird.shop.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 이메일 메세지 객체입니다.
 * to: 받는사람
 * subject: 메일 주제(제목)
 * message: 메일 메세지(본문)
 * bindingVariables: 메일 템플릿에 바인딩될 변수
 */
@Getter
@AllArgsConstructor @Builder
public class EmailMessage {
  private String to;
  private String subject;
  private String message;


  @Override
  public String toString() {
    return "EmailMessage{" +
        "to='" + to + '\'' +
        ", subject='" + subject + '\'' +
        ", message='" + message + '\'' +
        '}';
  }
}
