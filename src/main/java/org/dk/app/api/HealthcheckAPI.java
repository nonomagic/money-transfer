package org.dk.app.api;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class HealthcheckAPI {
    public static Route healthcheck = (Request request, Response response) -> {
        Map<String, Long> healthcheckResponse = new HashMap<>();
        healthcheckResponse.put("pong", System.currentTimeMillis() / 1000);
        return healthcheckResponse;
    };
}
