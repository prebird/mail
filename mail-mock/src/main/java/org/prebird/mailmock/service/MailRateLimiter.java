package org.prebird.mailmock.service;

public interface MailRateLimiter {
  boolean isAllowed(String account);
}
