package com.crm.record.bisync.service;

import com.crm.record.bisync.model.*;
import com.crm.record.bisync.service.handler.Handler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ContactService {

    private final Map<OperationType, Handler> handlerMap;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                        ProcessingContext context = new ProcessingContext(provider, operationType, requestBody, null, null, null, Instant.now());
                        handler.handle(context);
                        responses.add(new ContactResponse(context.getResponse()));
                    } catch (Exception e) {
                        responses.add(new ContactResponse("Failed to " + operationType + " contact: " + e.getMessage()));
                    }
                }
                break;
            case GET:
                try {
                    ProcessingContext context = new ProcessingContext(provider, operationType, null, null, null, id, Instant.now());
                    handler.handle(context);
                    responses.add(new ContactResponse(context.getResponse()));
                } catch (Exception e) {
                    responses.add(new ContactResponse("Failed to " + operationType + " contact: " + e.getMessage()));
                }
                break;
            case DELETE:
                for(String currId : ids) {
                    try {
                        ProcessingContext context = new ProcessingContext(provider, operationType, null, null, null, currId, Instant.now());
                        handler.handle(context);
                        responses.add(new ContactResponse(context.getResponse()));
                    } catch (Exception e) {
                        responses.add(new ContactResponse("Failed to " + operationType + " contact: " + e.getMessage()));
                    }
                }
                break;
            case SYNC:
                try {
                    ProcessingContext context = new ProcessingContext(provider, operationType, null, null, syncMessage, null, Instant.now());
                    handler.handle(context);
                    responses.add(new ContactResponse("Sync successful for provider: " + syncMessage.getProvider()));
                } catch (Exception e) {
                    responses.add(new ContactResponse("Sync Failed " + e.getMessage()));
                }
                break;
            default:
                System.out.println("Invalid Operation Type");
        }
        return responses;
    }

}