package com.crm.record.bisync.service.ruleEvaluation;

import com.crm.record.bisync.model.SyncMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class SyncRuleEngine {

    private List<SyncRule> syncRules = new ArrayList<>();

    //Load rules from config file

    @PostConstruct
    public void loadRules() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/sync-rules-config.json");
        SyncRulesWrapper rulesWrapper = objectMapper.readValue(is, SyncRulesWrapper.class);

        for (SyncRuleConfig config : rulesWrapper.getRules()) {
            SyncRule rule = SyncRuleFactory.createRule(config);
            syncRules.add(rule);
        }
    }

    //Invoke eligibility check

    public boolean isEligibleForSync(SyncMessage syncMessage) {
        for (SyncRule rule : syncRules) {
            if (!rule.isEligibleForSync(syncMessage)) {
                System.out.println("Rule failed: " + rule.getRuleType());
                return false;
            }
        }
        return true;
    }
}
