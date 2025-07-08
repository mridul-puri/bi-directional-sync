package com.crm.record.bisync.controller;

import com.crm.record.bisync.model.ContactResponse;
import com.crm.record.bisync.model.Contact;
import com.crm.record.bisync.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crm.record.bisync.model.SyncMessage;
import com.crm.record.bisync.service.SyncService;
import com.crm.record.bisync.model.OperationType;

import java.util.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    private final SyncService syncService;

    public ContactController(ContactService contactService, SyncService syncService) {
        this.contactService = contactService;
        this.syncService = syncService;
    }

    // Create API
    @PostMapping("{provider}/create")
    public ResponseEntity<List<ContactResponse>> createContacts(@PathVariable String provider,
                                                            @RequestBody List<String> requestBodies) {
        List<ContactResponse> responses = contactService.processContacts(provider, OperationType.CREATE, requestBodies, null, null, null, null);
        return ResponseEntity.ok(responses);
    }

    // Read API
    @GetMapping("{provider}/{id}")
    public ResponseEntity<ContactResponse> getContact(@PathVariable String provider,
                                              @PathVariable String id) {
        List<ContactResponse> responses = contactService.processContacts(provider, OperationType.GET, null, null, null, id, null);
        return ResponseEntity.ok(responses.get(0));
    }

    // Update API
    @PutMapping("{provider}/update")
    public ResponseEntity<List<ContactResponse>> updateContacts(@PathVariable String provider,
                                                                @RequestBody List<String> requestBodies) {
        List<ContactResponse> responses = contactService.processContacts(provider, OperationType.UPDATE, requestBodies, null, null, null, null);
        return ResponseEntity.ok(responses);
    }

    // Delete API
    @DeleteMapping("{provider}/delete")
    public ResponseEntity<List<ContactResponse>> deleteContacts(@PathVariable String provider,
                                                                @RequestBody List<String> ids) {
        List<ContactResponse> responses = contactService.processContacts(provider, OperationType.DELETE, null, null, null, null, ids);
        return ResponseEntity.ok(responses);
    }

    //Sync API
    @PostMapping("/sync")
    public ResponseEntity<ContactResponse> syncContact(@RequestBody SyncMessage syncMessage) {
        List<ContactResponse> responses = contactService.processContacts(null, OperationType.SYNC, null, null, syncMessage, null, null);
        return ResponseEntity.ok(responses.get(0));
    }
}
