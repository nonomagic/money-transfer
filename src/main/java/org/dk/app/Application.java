package org.dk.app;

import static org.dk.app.utils.RestUtils.errorResponse;
import static org.dk.app.utils.RestUtils.json;
import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

import org.dk.app.api.AccountAPI;
import org.dk.app.api.AccountOwnerAPI;
import org.dk.app.api.HealthcheckAPI;
import org.dk.app.model.dao.exceptions.DatabaseError;

public class Application {
    public static void main(String[] args) {
        port(Config.applicationPort);

        after((request, response) -> {
            response.type("application/json");
        });

        /*
         * Take care of mapping exceptions onto suitable error codes
         */
        notFound((reqest, response) ->
            "{\"error\": \"Requested resource not found.\"}"
        );
        exception(APIError.class, (error, request, response) -> {
            response.status(error.getErrorCode());
            response.body(errorResponse(error));
        });
        exception(DatabaseError.class, (error, request, response) -> {
            response.status(500);
            response.body(
                errorResponse(new APIError(error.getMessage(), 500))
            );
        });
        exception(RuntimeException.class, (error, request, response) -> {
            response.status(500);
            response.body(
                errorResponse(new APIError(error.getMessage(), 500))
            );
        });

        get("/ping", HealthcheckAPI.healthcheck, json());

        /*
         * Account owner API
         */
        get("/owners", AccountOwnerAPI.list, json());
        get("/owners/:id", AccountOwnerAPI.get, json());
        get("/owners/:id/accounts", AccountOwnerAPI.getAccounts, json());
        post("/owners", AccountOwnerAPI.create, json());
        delete("/owners/:id", AccountOwnerAPI.delete, json());

        /*
         * Account API
         */
        get("/accounts", AccountAPI.list, json());
        get("/accounts/:id", AccountAPI.get, json());
        post("/accounts", AccountAPI.create, json());
        put(
            "/accounts/:from_id/transfer/:to_id",
            AccountAPI.transfer, json()
        );
        delete("/accounts/:id", AccountAPI.delete, json());
    }
}
