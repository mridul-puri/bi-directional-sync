package com.crm.record.bisync.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ProcessingContext {

    public ProcessingContext(String provider, OperationType operationType, String requestBody, Contact contact, String id, Instant requestTime) {
        this.provider = provider;
        this.operationType = operationType;
        this.requestBody = requestBody;
        this.contact = contact;
        this.id = id;
        this.requestTime = requestTime;
    }

    private String provider;                      // CRM provider
    private OperationType operationType;          // CREATE, UPDATE, GET, DELETE, SYNC
    private String requestBody;                   // Raw input (for schema validation, transformation)
    private Contact contact;                      // Final Contact object for persistence
    private String id;                            // For GET / DELETE operation
    private String response;                      // API response per record
    private Instant requestTime;                  // API request timestamp for sync conflict resolution

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Instant getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Instant requestTime) {
        this.requestTime = requestTime;
    }

}
