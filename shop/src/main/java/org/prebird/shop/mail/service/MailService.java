package org.prebird.shop.mail.service;

import org.prebird.shop.mail.domain.EmailMessage;

public interface MailService {
  void send(EmailMessage emailMessage);
}
