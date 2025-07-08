package com.crm.record.bisync.service.ratelimiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.crm.record.bisync.config.RateLimitConfig;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final RateLimitConfig rateLimitConfig;

    public RateLimiterService(
            RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    public Bucket resolveBucket(String provider) {
        return buckets.computeIfAbsent(provider, this::createBucket);
    }

    private Bucket createBucket(String provider) {
        int limit = rateLimitConfig.getProviderLimits().getOrDefault(provider, rateLimitConfig.getDefaultLimit());
        Bandwidth limitBandwidth = Bandwidth.classic(limit, Refill.greedy(limit, Duration.ofMinutes(1)));
        return Bucket4j.builder().addLimit(limitBandwidth).build();
    }

    public boolean tryConsume(String provider) {
        Bucket bucket = resolveBucket(provider);
        return bucket.tryConsume(1);
    }
}
