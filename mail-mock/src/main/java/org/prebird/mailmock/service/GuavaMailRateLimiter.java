package org.prebird.mailmock.service;

import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;

@Service
public class GuavaMailRateLimiter implements MailRateLimiter {
  // 초당 5개로 요청 제한
  private static final int LIMIT_COUNT = 5;
  private final ConcurrentMap<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

  @Override
  public boolean limit(String account) {
    RateLimiter rateLimiter = rateLimiters.computeIfAbsent(account, newRate -> createRateLimiter());
    if (rateLimiter.tryAcquire()) {
      return true;
    }
    return false;
  }

  private RateLimiter createRateLimiter() {
    return RateLimiter.create(LIMIT_COUNT); // 초당 5개의 요청 허용
  }
}
