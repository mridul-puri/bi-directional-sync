package com.crm.record.bisync.service.ruleEvaluation;

import java.util.*;

public class SyncRuleFactory {

    //Create rule object from loaded config

    public static SyncRule createRule(SyncRuleConfig ruleConfig) {
        switch (ruleConfig.getRuleType()) {
            case "AllowedFieldsRule":
                String provider = (String) ruleConfig.getParams().get("provider");
                List<String> allowedFields = (List<String>) ruleConfig.getParams().get("allowedFields");
                return new AllowedFieldsRule(provider, allowedFields);

            // More rule types can be added here

            default:
                throw new IllegalArgumentException("Unknown rule type: " + ruleConfig.getRuleType());
        }
    }
}
