package com.crm.record.bisync.service;

import com.crm.record.bisync.model.SyncMessage;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.*;

@Service
public class SyncQueueService {

    //Configurable Retry Interval for Synchronization (to handle Rate Limiting at Destination)

    @Value("${retry.interval}")
    int retryInterval;

    //Blocking Queue to hold Sync messages

    private final BlockingQueue<SyncMessage> queue = new LinkedBlockingQueue<>();
    private final SyncProcessor syncProcessor;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public SyncQueueService(SyncProcessor syncProcessor) {
        this.syncProcessor = syncProcessor;
        startConsumer();
    }

    public void enqueue(SyncMessage message) {
        queue.offer(message);
    }

    //Send Sync Messages for Processing. Schedule Retries if failed

    private void startConsumer() {
        executor.scheduleWithFixedDelay(() -> {
            try {
                SyncMessage message = queue.take();
                boolean success = syncProcessor.process(message);

                if (!success) {
                    executor.schedule(() -> enqueue(message), retryInterval, TimeUnit.SECONDS);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
