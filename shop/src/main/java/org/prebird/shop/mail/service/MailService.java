package org.prebird.shop.mail.service;

import org.prebird.shop.mail.domain.EmailMessage;

public interface MailService {

  /**
   * 메일을 발송합니다.
   *
   * @param emailMessage 메일 객체
   *  메일 타입 (긴급/일반):
   *                  <ul>
   *                  <li>URGENT(긴급) : 1분 내에 발송됩니다.</li>
   *                  <li>NORMAL(일반) : 10분 내에 발송됩니다. (default)</li>
   *                  </ul>
   */
  void send(EmailMessage emailMessage);
}
