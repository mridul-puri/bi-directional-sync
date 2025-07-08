package com.crm.record.bisync.service.handler;

import com.crm.record.bisync.service.ratelimiting.RateLimiterService;
import com.crm.record.bisync.exception.RateLimitExceededException;
import com.crm.record.bisync.model.ProcessingContext;
import org.springframework.stereotype.Component;
import io.github.bucket4j.Bucket;

@Component
public class RateLimitingHandler extends AbstractHandler {

    private final RateLimiterService rateLimitingService;

    public RateLimitingHandler(RateLimiterService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    public void handle(ProcessingContext context) {
        String provider = context.getProvider();

        // Resolve the rate limit bucket for the CRM provider
        Bucket bucket = rateLimitingService.resolveBucket(provider);

        // If provider is not configured
        if (bucket == null) {
            throw new RateLimitExceededException("Unknown provider: " + provider);
        }

        // Check and consume a token from the bucket
        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded for provider: " + provider);
        }

        // Pass control to the next handler in the chain
        if (next != null) {
            next.handle(context);
        }
    }
}
