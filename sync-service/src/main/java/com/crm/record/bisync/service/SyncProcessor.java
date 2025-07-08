package com.crm.record.bisync.service;

import com.crm.record.bisync.config.DestinationConfig;
import org.springframework.web.client.RestTemplate;
import com.crm.record.bisync.model.SyncMessage;
import org.springframework.stereotype.Service;
import com.crm.record.bisync.exception.RateLimitExceededException;
import com.crm.record.bisync.service.transformation.TransformationService;
import com.crm.record.bisync.service.ruleEvaluation.SyncRuleEngine;
import com.crm.record.bisync.constants.Constants;

@Service
public class SyncProcessor {

    private final SyncRuleEngine syncRuleEngine;
    private final TransformationService transformationService;
    private final DestinationConfig destinationConfig;
    private final RestTemplate restTemplate;

    public SyncProcessor(SyncRuleEngine syncRuleEngine, TransformationService transformationService, DestinationConfig destinationConfig, RestTemplate restTemplate) {
        this.syncRuleEngine = syncRuleEngine;
        this.transformationService = transformationService;
        this.destinationConfig = destinationConfig;
        this.restTemplate = restTemplate;
    }

    //Process Sync Message (Rules Check -> Data Transformation -> Push to Destination

    public boolean process(SyncMessage message) {
        String flow = determineFlow(message);

        // Step 1: Check if message is eligible for sync using rule engine
        boolean isEligible = syncRuleEngine.isEligibleForSync(message);

        if (!isEligible) {
            System.out.println("Sync message rejected by rules: " + message);
            return false;
        }

        // Step 2: Transform data if eligible
        String transformedPayload = transformationService.transform(flow, message.getContact());
        message.setContact(transformedPayload);

        // Step 3: Send to external system
        try {
            String destination = message.getDestination();

            String destinationUrl = destinationConfig.getUrls().get(destination);
            pushToDestination(destinationUrl, message);

        } catch (RateLimitExceededException e) {
            return false; // Retry later
        }

        return true;
    }

    //Determine Flow for Data Transformation

    private String determineFlow(SyncMessage message) {
        String flow = null;
        switch(message.getSource()) {

            case Constants.Internal:
                flow = Constants.InternalToExternal;
                break;
            case Constants.External:
                flow = Constants.ExternalToInternal;
        }
        return flow;
    }

    private boolean pushToDestination(String url, SyncMessage syncMessage) {
        try {
            restTemplate.postForEntity(url, syncMessage, String.class);
            System.out.println("Sync message pushed to: " + url);
            return true;
        } catch (Exception e) {
            if (e.getMessage().contains("429")) {
                throw new RateLimitExceededException("Rate limit hit : " + syncMessage.getOperation() + "sync from " + syncMessage.getSource()
                        + " to " + syncMessage.getDestination() + " for provider : " + syncMessage.getProvider());
            }
        throw e;
        }
    }

}