package com.crm.record.bisync.service.validation;

public interface SchemaValidator {
    void validate(String provider, String requestBody);
}
