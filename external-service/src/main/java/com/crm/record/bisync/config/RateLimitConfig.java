package com.crm.record.bisync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitConfig {

    private Map<String, Integer> providerLimits = new HashMap<>();
    private int defaultLimit = 1000;

    public Map<String, Integer> getProviderLimits() {
        return providerLimits;
    }

    public void setProviderLimits(Map<String, Integer> providerLimits) {
        this.providerLimits = providerLimits;
    }

    public int getDefaultLimit() {
        return defaultLimit;
    }

    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }
}
