package org.prebird.mailmock.service;

import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class Bucket4JMailRateLimiter implements MailRateLimiter{
  private static final int LIMIT_COUNT = 5;
  private ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Override
  public boolean limit(String account) {
    Bucket bucket = buckets.computeIfAbsent(account, newBucket -> createNewBucket());
    return bucket.tryConsume(1);
  }

  private Bucket createNewBucket() {
    return Bucket.builder()
        .addLimit(limit -> limit.capacity(LIMIT_COUNT).refillGreedy(LIMIT_COUNT, Duration.ofSeconds(1)))
        .build();
  }
}
