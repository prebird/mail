package org.prebird.shop.mail.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이메일 메세지 객체입니다.
 * to: 받는사람
 * subject: 메일 주제(제목)
 * message: 메일 메세지(본문)
 * bindingVariables: 메일 템플릿에 바인딩될 변수
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
@AllArgsConstructor @Builder
@Entity
public class EmailMessage {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String toEmail;
  private String subject;
  private String message;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private EmailStatus emailStatus = EmailStatus.UNPROCESSED;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private EmailType emailType = EmailType.NORMAL;  // 이메일 타입 (긴급, 일반)

  private LocalDateTime sentAt; // 메일 발송 시간
  @Builder.Default
  private LocalDateTime requestAt = LocalDateTime.now(); // 메일 발송 요청 시간

  @Override
  public String toString() {
    return "EmailMessage{" +
        "to='" + toEmail + '\'' +
        ", subject='" + subject + '\'' +
        ", message='" + message + '\'' +
        '}';
  }

  /**
   *  이메일 메세지를 발송 완료 처리합니다.
   */
  public void completeSend() {
    this.emailStatus = EmailStatus.PROCESSED;
    this.sentAt = LocalDateTime.now();
  }

  public void changeEmailType(EmailType emailType) {
    this.emailType = emailType;
  }

  public void setSequenceAsId(long sequence) {
    this.id = sequence;
  }
}
