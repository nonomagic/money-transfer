package org.dk.app.controllers;

import org.dk.app.APIError;
import org.dk.app.Model;
import org.dk.app.model.Account;
import org.dk.app.model.AccountOwner;
import org.dk.app.model.dao.exceptions.DatabaseResorceAlreadyExistsError;
import org.dk.app.model.dao.exceptions.DatabaseResourceIsBusyError;

import java.util.Collection;
import java.util.Collections;

public class AccountOwnerController {
    public static Collection<AccountOwner> list() {
        return Model.accountOwnerDao.list();
    }

    public static AccountOwner create(String name, String email) {
        // Note: for simplicity I do not check the validity of an email
        if (name.equals("") || email.equals("")) {
            throw new APIError(
                "Name or email can not be empty", 400
            );
        }

        try {
            return Model.accountOwnerDao.create(name, email);
        } catch (DatabaseResorceAlreadyExistsError err) {
            throw new APIError(err.getMessage(), 409);
        }
    }

    public static AccountOwner get(String id) {
        AccountOwner owner = Model.accountOwnerDao.get(id);
        if (owner == null) {
            throw new APIError(
                String.format("Account owner with id \"%s\" does not exist", id),
                404
            );
        }

        return owner;
    }

    public static Collection<Account> getAccounts(String id) {
        AccountOwner owner = get(id);
        Collection<Account> accounts = Model.accountDao.listByOwner(owner.getId());
        if (accounts == null) {
            return Collections.EMPTY_LIST;
        }

        return accounts;
    }

    public static void delete(String id) {
        try {
            AccountOwner deleted = Model.accountOwnerDao.delete(id);
            if (deleted == null) {
                throw new APIError(
                    "Account owner " + id + " not found", 404
                );
            }
        } catch (DatabaseResourceIsBusyError error) {
            throw new APIError(
                String.format(
                    "Account owner \"%s\" can not be deleted as it has some accounts",
                    error.getResourceId()
                ), 400
            );
        }
    };
}
