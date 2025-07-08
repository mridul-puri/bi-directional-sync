package com.crm.record.bisync.service.ruleEvaluation;

import lombok.Data;
import java.util.List;

@Data
public class SyncRulesWrapper {

    private List<SyncRuleConfig> rules;

    public List<SyncRuleConfig> getRules() {
        return rules;
    }

    public void setRules(List<SyncRuleConfig> rules) {
        this.rules = rules;
    }
}
