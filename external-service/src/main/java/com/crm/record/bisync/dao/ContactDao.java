package com.crm.record.bisync.dao;

import com.crm.record.bisync.model.Contact;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ContactDao {

    //Storage (Contact Id -> Contact record mapping)

    private final Map<String, Contact> contactStore = new ConcurrentHashMap<>();

    public void save(Contact contact) {
        contactStore.put(contact.getId(), contact);
    }

    public Contact findById(String id) {
        return contactStore.get(id);
    }

    public Map<String, Contact> findAll() {
        return contactStore;
    }

    public void deleteById(String id) {
        contactStore.remove(id);
    }

    public void clear() {
        contactStore.clear();
    }
}
