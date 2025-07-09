package com.crm.record.bisync.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.*;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "destinations")
@Data
public class DestinationConfig {

    //Load configurations of Sync Destination

    private Map<String, String> urls = new HashMap<>();

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }

    public Map<String, String> getUrls() {
        return urls;
    }
}