package org.dk.app.api;

import static org.dk.app.utils.RestUtils.readJson;

import com.fasterxml.jackson.databind.JsonNode;
import org.dk.app.controllers.AccountOwnerController;
import org.dk.app.model.Account;
import org.dk.app.model.AccountOwner;
import org.dk.app.utils.RestUtils;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class AccountOwnerAPI {
    public static Route list = (Request request, Response response) -> {
        Map<String, Iterable<AccountOwner>> owners = new HashMap<>();
        owners.put("owners", AccountOwnerController.list());
        return owners;
    };

    public static Route getAccounts = (Request request, Response response) -> {
        Map<String, Iterable<Account>> listResponse = new HashMap<>();
        listResponse.put(
            "accounts",
            AccountOwnerController.getAccounts(request.params("id"))
        );
        return listResponse;
    };

    public static Route create = (Request request, Response response) -> {
        JsonNode reqJson = readJson(request.body());
        String name = reqJson.path("name").asText("");
        String email = reqJson.path("email").asText("a");

        return AccountOwnerController.create(name, email);
    };

    public static Route get = (Request request, Response response) -> {
        AccountOwner owner = AccountOwnerController.get(request.params(":id"));
        if (owner == null) {
            return RestUtils.EMPTY_RESPONSE;
        }

        return owner;
    };

    public static Route delete = (Request request, Response response) -> {

        AccountOwnerController.delete(request.params(":id"));

        return RestUtils.EMPTY_RESPONSE;
    };

}
