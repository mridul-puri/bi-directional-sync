package com.crm.record.bisync.controller;

import com.crm.record.bisync.model.SyncMessage;
import com.crm.record.bisync.service.SyncQueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync")
public class SyncController {

    private final SyncQueueService syncQueueService;

    public SyncController(SyncQueueService syncQueueService) {
        this.syncQueueService = syncQueueService;
    }

    @PostMapping("/enqueue")
    public ResponseEntity<String> enqueueSyncMessage(@RequestBody SyncMessage message) {
        syncQueueService.enqueue(message);
        return ResponseEntity.ok("Message enqueued successfully");
    }
}
