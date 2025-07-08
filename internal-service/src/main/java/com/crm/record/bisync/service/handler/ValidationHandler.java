package com.crm.record.bisync.service.handler;

import com.crm.record.bisync.service.validation.JSONSchemaValidatorService;
import com.crm.record.bisync.exception.SchemaValidationException;
import com.crm.record.bisync.model.ProcessingContext;

public class ValidationHandler extends AbstractHandler {

    private final JSONSchemaValidatorService schemaValidator;

    public ValidationHandler(JSONSchemaValidatorService schemaValidator) {
        this.schemaValidator = schemaValidator;
    }

    //Invoke Schema Validation

    @Override
    public void handle(ProcessingContext context) {
        String provider = context.getProvider();
        String requestBody = context.getRequestBody();
        try {
        schemaValidator.validate(provider, requestBody);
        } catch (Exception e) {
            throw new SchemaValidationException("Schema validation failed for provider " + provider + ": " + e.getMessage());
        }
        if (next != null) {
            next.handle(context);
        }
    }
}
