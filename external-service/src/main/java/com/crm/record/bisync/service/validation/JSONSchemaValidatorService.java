package com.crm.record.bisync.service.validation;

import com.crm.record.bisync.constants.Constants;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class JSONSchemaValidatorService implements SchemaValidator {

    private final Map<String, Schema> schemaMap = new HashMap<>();

    //Load JSON Schemas and validate data

    @PostConstruct
    public void loadSchemas() {
        loadSchema(Constants.CRM1, "/schemas/crm1-schema.json");
        loadSchema(Constants.CRM2, "/schemas/crm2-schema.json");
        loadSchema(Constants.CRM3, "/schemas/crm3-schema.json");
    }

    private void loadSchema(String provider, String schemaPath) {
        InputStream schemaStream = getClass().getResourceAsStream(schemaPath);
        if (schemaStream == null) {
            throw new RuntimeException("Schema file not found: " + schemaPath);
        }
        JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
        Schema schema = SchemaLoader.load(rawSchema);
        schemaMap.put(provider, schema);
    }

    @Override
    public void validate(String provider, String requestBody) {
        Schema schema = schemaMap.get(provider);
        if (schema == null) {
            throw new RuntimeException("No schema configured for provider: " + provider);
        }

        JSONObject jsonObject = new JSONObject(requestBody);
        schema.validate(jsonObject);
    }
}
