package com.crm.record.bisync.service.handler;

import com.crm.record.bisync.constants.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crm.record.bisync.model.ProcessingContext;
import com.crm.record.bisync.model.SyncMessage;
import com.crm.record.bisync.client.SyncClient;


public class OutgoingSyncHandler extends AbstractHandler {

    private final SyncClient syncClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OutgoingSyncHandler(SyncClient syncClient) {
        this.syncClient = syncClient;
    }

    //Prepare message for synchronization

    @Override
    public void handle(ProcessingContext context) {

        SyncMessage syncMessage = new SyncMessage();
        syncMessage.setProvider(context.getProvider());
        syncMessage.setSource(Constants.INTERNAL);
        syncMessage.setOperation(Constants.CREATE);

        //Convert transformed object to String for generic synchronization
        try {
            syncMessage.setContact(objectMapper.writeValueAsString(context.getContact()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        syncMessage.setDestination(Constants.EXTERNAL);
        syncMessage.setSyncTimestamp(context.getRequestTime().toString()); //Passed as String to avoid DataType mismatch during Sync (System Agnostic)

        // Send to Sync Service
        syncClient.sendSyncMessage(syncMessage);

        if (next != null) {
            next.handle(context);
        }
    }

}
