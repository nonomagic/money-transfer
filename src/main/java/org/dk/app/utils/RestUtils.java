package org.dk.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dk.app.APIError;
import spark.ResponseTransformer;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RestUtils {
    public static final Map<String, String> EMPTY_RESPONSE =
        Collections.unmodifiableMap(new HashMap<>());

    private static final ObjectMapper om = new ObjectMapper();

    public static String toJson(Object data) {
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            return om.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                "Failed to serialize (" + data + ") to JSON", e
            );
        }
    }

    public static JsonNode readJson(String data) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readTree(data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON");
        }
    }
    
    public static ResponseTransformer json() {
        return RestUtils::toJson;
    }

    public static String errorResponse(APIError error) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", error.getMessage());
        return toJson(errorResponse);
    }
}
