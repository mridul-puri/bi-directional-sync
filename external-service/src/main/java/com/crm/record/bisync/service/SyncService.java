package com.crm.record.bisync.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import com.crm.record.bisync.model.SyncMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crm.record.bisync.model.Contact;
import com.crm.record.bisync.dao.ContactDao;
import java.time.Instant;
import com.crm.record.bisync.constants.Constants;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class SyncService {

    private final ContactDao contactDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SyncService(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    //Process incoming synchronized message based on Operation
    // Update storage according to lastUpdatedTimestamp for Conflict Resolution)

    public void processSync(SyncMessage syncMessage) {
        String operation = syncMessage.getOperation();
        String contact = syncMessage.getContact();

        // Convert String synchronized message to Contact object
        Contact incomingContact = null;
        try {
            incomingContact = objectMapper.readValue(contact, Contact.class);
        } catch (JsonProcessingException e) {
            System.out.println("Unable to recognize synchronized message");
            return;
        }

        String contactId = incomingContact.getId();

        switch (operation.toUpperCase()) {
            case Constants.CREATE:
                handleCreate(contactId, incomingContact, syncMessage.getSyncTimestamp());
                break;

            case Constants.UPDATE:
                handleUpdate(contactId, incomingContact, syncMessage.getSyncTimestamp());
                break;

            case Constants.DELETE:
                handleDelete(contactId, incomingContact, syncMessage.getSyncTimestamp());
                break;

            default:
                System.out.println("Unsupported operation: " + operation);
        }
    }

    private void handleCreate(String contactId, Contact incomingContact, String syncTimestamp) {
        Contact existingContact = contactDao.findById(contactId);
        if (existingContact == null) {
            incomingContact.setLastUpdated(syncTimestamp);
            contactDao.save(incomingContact);
        } else {
            // Conflict Resolution: Last-Write-Wins
            if (formatTime(syncTimestamp).isAfter(formatTime(existingContact.getLastUpdated()))) {
                incomingContact.setLastUpdated(syncTimestamp);
                contactDao.save(incomingContact);
            }
        }
    }

    private void handleUpdate(String contactId, Contact incomingContact, String syncTimestamp) {
        Contact existingContact = contactDao.findById(contactId);
        if (existingContact == null) {
            // If no record exists, treat as create
            incomingContact.setLastUpdated(syncTimestamp);
            contactDao.save(incomingContact);
        } else {
            // Conflict Resolution: Last-Write-Wins
            if (formatTime(syncTimestamp).isAfter(formatTime(existingContact.getLastUpdated()))) {
                incomingContact.setLastUpdated(syncTimestamp);
                contactDao.save(incomingContact);
            }
        }
    }


    private void handleDelete(String contactId, Contact incomingContact, String syncTimestamp) {
        Contact existingContact = contactDao.findById(contactId);
        if (existingContact != null) {
            contactDao.deleteById(contactId);
        }
    }

    private Instant formatTime(String timestamp) {
        double epochSeconds = Double.parseDouble(timestamp);
        return Instant.ofEpochSecond((long) epochSeconds, (long) ((epochSeconds % 1) * 1_000_000_000));
    }
}
