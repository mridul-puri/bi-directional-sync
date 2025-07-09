package com.crm.record.bisync.service.transformation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Service
public class TransformationServiceImpl implements TransformationService {

    private final MappingLoaderService mappingLoaderService;
    private final ObjectMapper objectMapper;

    public TransformationServiceImpl(MappingLoaderService mappingLoaderService) {
        this.mappingLoaderService = mappingLoaderService;
        this.objectMapper = new ObjectMapper();
    }

    //Method to transform data using mappings

    @Override
    public String transform(String flow, String requestBody) {
        Map<String, String> mapping = mappingLoaderService.getMappingForProvider(flow);
        ObjectNode contactJsonNode = objectMapper.createObjectNode();

        mapping.forEach((sourceJsonPath, destinationPath) -> {
            try {
                Object value = JsonPath.read(requestBody, sourceJsonPath);
                if (value != null) {
                    setJsonValue(contactJsonNode, destinationPath, value.toString());
                }
            } catch (Exception e) {
                System.out.println("Warning: Field missing or invalid for path: " + sourceJsonPath);
            }
        });

        try {
            return objectMapper.writeValueAsString(contactJsonNode);
        } catch (JsonProcessingException e) {
            System.out.println("Transformation failed");
            return StringUtils.EMPTY;
        }
    }

    private void setJsonValue(ObjectNode root, String jsonPointer, String value) {
        String[] pathParts = jsonPointer.substring(1).split("/");
        ObjectNode currentNode = root;

        for (int i = 0; i < pathParts.length - 1; i++) {
            String part = pathParts[i];
            if (!currentNode.has(part) || !currentNode.get(part).isObject()) {
                currentNode.set(part, objectMapper.createObjectNode());
            }
            currentNode = (ObjectNode) currentNode.get(part);
        }

        currentNode.put(pathParts[pathParts.length - 1], value);
    }
}
