package com.ratelimiter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 100;
    private static final Duration WINDOW = Duration.ofSeconds(60);

    public boolean isAllowed(String clientId) {
        String key = "rate_limit:" + clientId;
        Long currentCount = redisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
            redisTemplate.expire(key, WINDOW);
        }
        return currentCount <= MAX_REQUESTS;
    }

    public long getRemainingRequests(String clientId) {
        String key = "rate_limit:" + clientId;
        String val = redisTemplate.opsForValue().get(key);
        if (val == null) return MAX_REQUESTS;
        return Math.max(0, MAX_REQUESTS - Long.parseLong(val));
    }
}
