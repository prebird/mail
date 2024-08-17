package org.prebird.mailmock.service;

import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("guava")
@Service
public class GuavaMailRateLimiter implements MailRateLimiter {
  private static final int LIMIT_COUNT = 5; // 초당 5개로 요청 제한
  private final ConcurrentMap<String, RateLimiter> rateLimiters
      = new ConcurrentHashMap<>();

  @Override
  public boolean isAllowed(String account) {
    RateLimiter rateLimiter = rateLimiters.computeIfAbsent(account,
        newRate -> createRateLimiter());
    return rateLimiter.tryAcquire();
  }

  private RateLimiter createRateLimiter() {
    return RateLimiter.create(LIMIT_COUNT); // 초당 5개의 요청 허용
  }
}
