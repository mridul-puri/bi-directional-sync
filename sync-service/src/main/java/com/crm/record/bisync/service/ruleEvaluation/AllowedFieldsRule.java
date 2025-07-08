package com.crm.record.bisync.service.ruleEvaluation;

import com.crm.record.bisync.model.SyncMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class AllowedFieldsRule implements SyncRule {

    private String provider;
    private List<String> allowedFields;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public AllowedFieldsRule(String provider, List<String> allowedFields) {
        this.provider = provider;
        this.allowedFields = allowedFields;
    }

    //Check Sync Action against configured rules (If all rules pass -> allow)

    @Override
    public boolean isEligibleForSync(SyncMessage syncMessage) {
        if (!syncMessage.getProvider().equalsIgnoreCase(provider)) {
            return true;
        }


        try {
            // Parse JSON string to Map
            Map<String, Object> contactMap = objectMapper.readValue(syncMessage.getContact(), Map.class);

            // Check if all fields are allowed
            return contactMap.keySet().stream().allMatch(field -> allowedFields.contains(field));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getRuleType() {
        return "AllowedFieldsRule";
    }
}