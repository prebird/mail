package org.prebird.mailworker.service;


import org.prebird.mailworker.domain.EmailMessage;

public interface MailService {
  void send(EmailMessage emailMessage);
}
