package com.ratelimiter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RateLimitResponse {
    private String error;
    private int retryAfter;
    private long timestamp;
}
