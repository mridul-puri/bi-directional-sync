package com.crm.record.bisync.service;

import com.crm.record.bisync.model.ContactResponse;
import com.crm.record.bisync.model.Contact;
import com.crm.record.bisync.model.ProcessingContext;
import com.crm.record.bisync.model.SyncMessage;
import com.crm.record.bisync.service.handler.Handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import com.crm.record.bisync.model.OperationType;

import java.time.Instant;
import java.util.*;

@Service
public class ContactService {

    private final Map<OperationType, Handler> handlerMap;
    ObjectMapper objectMapper = new ObjectMapper();

    public ContactService(Map<OperationType, Handler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    //Invoke Chain of Responsibility (As defined in HandlerConfig) and process APIs

    public List<ContactResponse> processContacts(String provider, OperationType operationType, List<String> requestBodies, Contact contact, SyncMessage syncMessage, String id, List<String> ids) {
        List<ContactResponse> responses = new ArrayList<>();
        Handler handler = handlerMap.get(operationType);
        switch (operationType) {
            case CREATE, UPDATE:
                for (String requestBody : requestBodies) {
                    try {
                        ProcessingContext context = new ProcessingContext(provider, operationType, requestBody, null, null, Instant.now());
                        handler.handle(context);
                        responses.add(new ContactResponse(context.getResponse()));
                    } catch (Exception e) {
                        responses.add(new ContactResponse("Failed to " + operationType + " contact: " + e.getMessage()));
                    }
                }
                break;
            case GET:
                try {
                    ProcessingContext context = new ProcessingContext(provider, operationType, null, null, id, Instant.now());
                    handler.handle(context);
                    responses.add(new ContactResponse(context.getResponse()));
                } catch (Exception e) {
                    responses.add(new ContactResponse("Failed to " + operationType + " contact: " + e.getMessage()));
                }
                break;
            case DELETE:
                for(String currId : ids) {
                    try {
                        ProcessingContext context = new ProcessingContext(provider, operationType, null, null, currId, Instant.now());
                        handler.handle(context);
                        responses.add(new ContactResponse(context.getResponse()));
                    } catch (Exception e) {
                        responses.add(new ContactResponse("Failed to " + operationType + " contact: " + e.getMessage()));
                    }
                }
                break;
            default:
                System.out.println("Invalid Operation Type");
        }
        return responses;
    }
}