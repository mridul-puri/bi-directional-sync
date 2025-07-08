package com.crm.record.bisync.service.transformation;

import com.crm.record.bisync.constants.Constants;
import com.crm.record.bisync.model.Contact;
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
    public <T> T transform(String provider, String requestBody) {
        T response = null;
        Map<String, String> mapping = mappingLoaderService.getMappingForProvider(provider);
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

        //Convert transformed object depending on direction of transformation

        try {
            response = provider.endsWith(Constants.REVERSE) ? (T) objectMapper.writeValueAsString(contactJsonNode) : (T) objectMapper.convertValue(contactJsonNode, Contact.class);
        } catch (JsonProcessingException e) {
            System.out.println("Transformation failed for provider : " + provider);
            return (T) StringUtils.EMPTY;
        }

        return response;
    }

    private void setJsonValue(ObjectNode root, String jsonPointer, String value) {
        String[] pathParts = jsonPointer.substring(1).split("/"); // remove leading slash and split
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
