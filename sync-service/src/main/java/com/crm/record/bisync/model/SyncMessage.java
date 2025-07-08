package com.crm.record.bisync.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncMessage {

    private String provider;
    private String source;
    private String operation; // "CREATE", "UPDATE", "DELETE"
    private String contact;
    private String destination;
    private Instant syncTimestamp; // Timestamp to use for last-write-wins

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Instant getSyncTimestamp() {
        return syncTimestamp;
    }

    public void setSyncTimestamp(Instant syncTimestamp) {
        this.syncTimestamp = syncTimestamp;
    }
}
