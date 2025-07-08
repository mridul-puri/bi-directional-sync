package com.crm.record.bisync.service.handler;

import com.crm.record.bisync.constants.Constants;
import com.crm.record.bisync.dao.ContactDao;
import com.crm.record.bisync.exception.RecordNotFoundException;
import com.crm.record.bisync.model.Contact;
import com.crm.record.bisync.model.ProcessingContext;

public class PersistenceHandler extends AbstractHandler {

    private final ContactDao contactDao;

    public PersistenceHandler(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Override
    public void handle(ProcessingContext context) {
        switch(context.getOperationType()) {
            case CREATE:
                context.getContact().setLastUpdated(context.getRequestTime().toString());
                contactDao.save(context.getContact());
                context.setResponse("Contact created successfully");
                break;
            case UPDATE:
                Contact existingContact = contactDao.findById(context.getContact().getId());
                if (existingContact == null) {
                    throw new RuntimeException("Contact not found with id: " + context.getContact().getId());
                }
                context.getContact().setLastUpdated(context.getRequestTime().toString());
                contactDao.save(context.getContact());
                context.setResponse("Contact updated successfully");
                break;
            case GET:
                Contact contact = contactDao.findById(context.getId());
                if (contact == null) {
                    throw new RecordNotFoundException("Contact not found with id: " + context.getId());
                }
                context.setProvider(context.getProvider() + Constants.REVERSE);
                context.setContact(contact);
                break;
            case DELETE:
                try {
                    Contact existing = contactDao.findById(context.getId());
                    if (existing == null) {
                        throw new RuntimeException("Contact not found with id: " + context.getId());
                    }
                    contactDao.deleteById(context.getId());
                    context.setResponse("Contact deleted successfully");
                } catch (Exception e) {
                    context.setResponse("Failed to delete contact: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Invalid Operation Type");

        }
        if (next != null) {
            next.handle(context);
        }
    }
}
