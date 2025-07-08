package com.crm.record.bisync.service.transformation;

public interface TransformationService {
    <T> T transform(String provider, String requestBody);
}
