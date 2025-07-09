package com.crm.record.bisync.controller;

import com.crm.record.bisync.model.ContactResponse;
import com.crm.record.bisync.model.OperationType;
import com.crm.record.bisync.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/internal/contacts/v1")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Create API
    @PostMapping("{provider}/create")
    public ResponseEntity<List<ContactResponse>> createContacts(@PathVariable("provider") String provider,
                                                                @RequestBody List<String> requestBodies) {
        List<ContactResponse> responses = contactService.processContacts(provider, OperationType.CREATE, requestBodies, null, null, null, null);
        return ResponseEntity.ok(responses);
    }

    // Read API
    @GetMapping("{provider}/{id}")
    public ResponseEntity<ContactResponse> getContact(@PathVariable("provider") String provider,
                                                      @PathVariable("id") String id) {
        List<ContactResponse> responses = contactService.processContacts(provider, OperationType.GET, null, null, null, id, null);
        return ResponseEntity.ok(responses.get(0));
    }

    // Update API
    @PutMapping("{provider}/update")
    public ResponseEntity<List<ContactResponse>> updateContacts(@PathVariable("provider") String provider,
                                                                @RequestBody List<String> requestBodies) {
        List<ContactResponse> responses = contactService.processContacts(provider, OperationType.UPDATE, requestBodies, null, null, null, null);
        return ResponseEntity.ok(responses);
    }

    // Delete API
    @DeleteMapping("{provider}/delete")
    public ResponseEntity<List<ContactResponse>> deleteContacts(@PathVariable("provider") String provider,
                                                                @RequestBody List<String> ids) {
        List<ContactResponse> responses = contactService.processContacts(provider, OperationType.DELETE, null, null, null, null, ids);
        return ResponseEntity.ok(responses);
    }
}
