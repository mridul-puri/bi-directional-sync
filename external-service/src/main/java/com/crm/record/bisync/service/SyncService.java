package com.crm.record.bisync.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import com.crm.record.bisync.model.SyncMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crm.record.bisync.model.Contact;
import com.crm.record.bisync.dao.ContactDao;
import java.time.Instant;
import com.crm.record.bisync.constants.Constants;

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
                handleCreate(contactId, incomingContact);
                break;

            case Constants.UPDATE:
                handleUpdate(contactId, incomingContact);
                break;

            case Constants.DELETE:
                handleDelete(contactId, incomingContact);
                break;

            default:
                System.out.println("Unsupported operation: " + operation);
        }
    }

    private void handleCreate(String contactId, Contact incomingContact) {
        Contact existingContact = contactDao.findById(contactId);
        if (existingContact == null) {
            contactDao.save(incomingContact);
        } else {
            // Conflict Resolution: Last-Write-Wins
            if (Instant.parse(incomingContact.getLastUpdated()).isAfter(Instant.parse(existingContact.getLastUpdated()))) {
                contactDao.save(incomingContact);
            }
        }
    }

    private void handleUpdate(String contactId, Contact incomingContact) {
        Contact existingContact = contactDao.findById(contactId);
        if (existingContact == null) {
            // If no record exists, treat as create
            contactDao.save(incomingContact);
        } else {
            // Conflict Resolution: Last-Write-Wins
            if (Instant.parse(incomingContact.getLastUpdated()).isAfter(Instant.parse(existingContact.getLastUpdated()))) {
                contactDao.save(incomingContact);
            }
        }
    }

    private void handleDelete(String contactId, Contact incomingContact) {
        Contact existingContact = contactDao.findById(contactId);
        // Conflict Resolution: Last-Write-Wins
        if (existingContact != null && Instant.parse(incomingContact.getLastUpdated()).isAfter(Instant.parse(existingContact.getLastUpdated()))) {
            contactDao.deleteById(contactId);
        }
    }
}
