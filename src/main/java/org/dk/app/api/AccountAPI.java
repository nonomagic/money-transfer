package org.dk.app.api;

import static org.dk.app.utils.RestUtils.readJson;

import com.fasterxml.jackson.databind.JsonNode;
import org.dk.app.APIError;
import org.dk.app.controllers.AccountController;
import org.dk.app.model.Account;
import org.dk.app.utils.RestUtils;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AccountAPI {
    public static Route list = (Request request, Response response) -> {
        Map<String, Collection<Account>> listResponse = new HashMap<>();
        listResponse.put("accounts", AccountController.list());
        return listResponse;
    };

    public static Route get = (Request request, Response response) -> {
        Account account = AccountController.get(request.params(":id"));
        if (account == null) {
            return RestUtils.EMPTY_RESPONSE;
        }

        return account;
    };

    public static Route create = (Request request, Response response) -> {
        JsonNode reqJson = readJson(request.body());
        JsonNode ownerIdNode = reqJson.get("ownerId");
        JsonNode balanceNode = reqJson.get("balance");

        if (ownerIdNode == null || balanceNode == null) {
            throw new APIError("ownerId or balance is not present", 400);
        }

        String ownerId = ownerIdNode.asText();
        if (!balanceNode.isNumber()) {
            throw new APIError("Balance has to be a number", 400);
        }

        return AccountController.create(ownerId, balanceNode.asDouble());
    };

    public static Route transfer = (Request request, Response response) -> {
        String fromId = request.params(":from_id");
        String toId = request.params(":to_id");

        JsonNode amountNode = readJson(request.body()).get("amount");
        if (amountNode == null || !amountNode.isNumber()) {
            throw new APIError(
                "Amount of transfer is missing or not a number",
                400
            );
        }

        AccountController.transfer(fromId, toId, amountNode.asDouble());
        return RestUtils.EMPTY_RESPONSE;
    };

    public static Route delete = (Request request, Response response) -> {
        AccountController.delete(request.params(":id"));
        return RestUtils.EMPTY_RESPONSE;
    };
}
