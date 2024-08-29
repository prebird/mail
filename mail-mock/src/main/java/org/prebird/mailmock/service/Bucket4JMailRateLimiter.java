package org.prebird.mailmock.service;

import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("bucket4j")
@Service
public class Bucket4JMailRateLimiter implements MailRateLimiter{
  private static final int LIMIT_COUNT = 10; // 초당 5회 제한
  private ConcurrentHashMap<String, Bucket> buckets
      = new ConcurrentHashMap<>();

  /**
   * 계정별로 요청 제한을 적용하고, 처리 가능한지 반환합니다.
   * @param account
   * @return 요청 처리 가능 여부
   */
  @Override
  public boolean isAllowed(String account) {
    Bucket bucket = buckets.computeIfAbsent(account,
        newBucket -> createNewBucket());
    return bucket.tryConsume(1);
  }

  private Bucket createNewBucket() {
    return Bucket.builder()
        .addLimit(limit -> limit.capacity(LIMIT_COUNT).refillGreedy(LIMIT_COUNT
            , Duration.ofSeconds(1)))
        .build();
  }
}
