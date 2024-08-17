package org.prebird.mailmock.service;

public interface MailRateLimiter {
  boolean limit(String account);
}
