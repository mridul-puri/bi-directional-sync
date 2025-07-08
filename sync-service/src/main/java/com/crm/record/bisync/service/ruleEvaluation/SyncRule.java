package com.crm.record.bisync.service.ruleEvaluation;

import com.crm.record.bisync.model.SyncMessage;

public interface SyncRule {
    boolean isEligibleForSync(SyncMessage syncMessage);
    String getRuleType();
}
