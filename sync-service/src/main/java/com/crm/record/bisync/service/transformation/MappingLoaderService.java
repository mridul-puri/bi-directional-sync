package com.crm.record.bisync.service.transformation;

import com.crm.record.bisync.constants.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class MappingLoaderService {

    private final Map<String, Map<String, String>> mappingCache = new HashMap<>();

    // Load Mappings for Data Transformation (Source -> Destination)

    @PostConstruct
    public void loadMappings() {
        loadMapping(Constants.InternalToExternal, "/mappings/internal-external.json");
        loadMapping(Constants.ExternalToInternal, "/mappings/external-internal.json");
    }

    private void loadMapping(String flow, String mappingPath) {
        try (InputStream is = getClass().getResourceAsStream(mappingPath)) {
            if (is == null) throw new RuntimeException("Mapping file not found: " + mappingPath);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> mapping = mapper.readValue(is, new TypeReference<>() {});
            mappingCache.put(flow, mapping);
        } catch (Exception e) {
            throw new RuntimeException("Error loading mapping for " + flow, e);
        }
    }

    public Map<String, String> getMappingForProvider(String flow) {
        return mappingCache.get(flow);
    }
}
