package com.crm.record.bisync.service.handler;

import com.crm.record.bisync.model.ProcessingContext;

public interface Handler {
    void setNext(Handler nextHandler);
    void handle(ProcessingContext context);
}