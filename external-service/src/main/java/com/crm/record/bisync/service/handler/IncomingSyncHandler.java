package com.crm.record.bisync.service.handler;

import com.crm.record.bisync.model.ProcessingContext;
import com.crm.record.bisync.service.SyncService;

public class IncomingSyncHandler extends AbstractHandler {

    private final SyncService syncService;

    public IncomingSyncHandler(SyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    public void handle(ProcessingContext context) {
        syncService.processSync(context.getSyncMessage());
        if (next != null) {
            next.handle(context);
        }
    }

}
