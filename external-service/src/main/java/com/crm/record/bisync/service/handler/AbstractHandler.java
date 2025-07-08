package com.crm.record.bisync.service.handler;

import com.crm.record.bisync.model.ProcessingContext;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public abstract class AbstractHandler implements Handler {

    protected Handler next;

    @Override
    public void setNext(Handler nextHandler) {
        this.next = nextHandler;
    }

    @Override
    public abstract void handle(ProcessingContext context);
}
