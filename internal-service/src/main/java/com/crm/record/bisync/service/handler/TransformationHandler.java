package com.crm.record.bisync.service.handler;

import com.crm.record.bisync.constants.Constants;
import com.crm.record.bisync.service.transformation.TransformationService;
import com.crm.record.bisync.model.ProcessingContext;
import com.crm.record.bisync.model.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransformationHandler extends AbstractHandler {

    private final TransformationService transformationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransformationHandler(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    //Data Transformation (Inbound and Outbound)

    @Override
    public void handle(ProcessingContext context) {
        if(context.getProvider().endsWith(Constants.REVERSE)) {
            //Transformation for GET API

            try {
                String contact = transformationService.transform(context.getProvider(), objectMapper.writeValueAsString(context.getContact()));
                context.setResponse(contact);
            } catch (JsonProcessingException e) {
                System.out.println("Transformation Failure for Provider : " + context.getProvider() + " with Id : " + context.getId());
            }
        } else {
            // Transformation for CREATE / UPDATE API

            Contact contact = transformationService.transform(context.getProvider(), context.getRequestBody());
            context.setContact(contact);
        }
        if (next != null) {
            next.handle(context);
        }
    }
}
