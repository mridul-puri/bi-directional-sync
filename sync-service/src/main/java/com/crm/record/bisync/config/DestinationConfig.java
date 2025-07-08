package com.crm.record.bisync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.*;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "destinations")
@Data
public class DestinationConfig {

    //Load configurations of Sync Destination

    private Map<String, String> urls = new HashMap<>();

    public Map<String, String> getUrls() {
        return urls;
    }
}