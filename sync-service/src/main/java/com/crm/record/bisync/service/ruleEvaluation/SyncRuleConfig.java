package com.crm.record.bisync.service.ruleEvaluation;

import lombok.Data;
import java.util.Map;

@Data
public class SyncRuleConfig {
    private String ruleType;
    private Map<String, Object> params;  // Flexible parameters per rule

    public Map<String, Object> getParams() {
        return params;
    }

    public String getRuleType() {
        return ruleType;
    }
}