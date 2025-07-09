package com.crm.record.bisync.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.crm.record.bisync.model.SyncMessage;

@Service
public class SyncClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private String syncBaseUrl;
    private String SYNC_SERVICE_URL;

    public SyncClient(@Value("${sync.service.base.url}") String syncBaseUrl) {
        this.syncBaseUrl = syncBaseUrl;
        this.SYNC_SERVICE_URL = syncBaseUrl + "/enqueue";
    }

    //Send Synchronization message to sync service

    public void sendSyncMessage(SyncMessage syncMessage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            HttpEntity<SyncMessage> requestEntity = new HttpEntity<>(syncMessage, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    SYNC_SERVICE_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            System.out.println("Sync Message Sent: " + response.getStatusCode());

        } catch (Exception e) {
            System.err.println("Error sending sync message: " + e.getMessage());
        }
    }
}
