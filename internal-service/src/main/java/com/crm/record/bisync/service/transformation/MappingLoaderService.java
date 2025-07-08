package com.crm.record.bisync.service.transformation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import com.crm.record.bisync.constants.Constants;

@Service
public class MappingLoaderService {

    private final Map<String, Map<String, String>> mappingCache = new HashMap<>();

    // Load Mappings for Data Transformation (Source -> Destination)

    @PostConstruct
    public void loadMappings() {
        loadMapping(Constants.CRM1, "/mappings/crm1-mapping.json");
        loadMapping(Constants.CRM2, "/mappings/crm2-mapping.json");
        loadMapping(Constants.CRM3, "/mappings/crm3-mapping.json");
        loadMapping("CRM1-Reverse", "/mappings/crm1-mapping-reverse.json");
        loadMapping("CRM2-Reverse", "/mappings/crm2-mapping-reverse.json");
        loadMapping("CRM3-Reverse", "/mappings/crm3-mapping-reverse.json");
    }

    private void loadMapping(String provider, String mappingPath) {
        try (InputStream is = getClass().getResourceAsStream(mappingPath)) {
            if (is == null) throw new RuntimeException("Mapping file not found: " + mappingPath);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> mapping = mapper.readValue(is, new TypeReference<>() {});
            mappingCache.put(provider, mapping);
        } catch (Exception e) {
            throw new RuntimeException("Error loading mapping for " + provider, e);
        }
    }

    public Map<String, String> getMappingForProvider(String provider) {
        return mappingCache.get(provider);
    }
}
