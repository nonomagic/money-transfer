package org.dk.app.controllers;

import org.dk.app.APIError;
import org.dk.app.Model;
import org.dk.app.model.Account;
import org.dk.app.model.AccountOwner;
import org.dk.app.model.dao.exceptions.DatabaseAccountTransferError;

import java.util.Collection;

public class AccountController {
    public static Collection<Account> list() {
        return Model.accountDao.list();
    }

    public static Account get(String id) {
        return getAccount(id);
    }

    public static Account create(String ownerId, double balance) {
        AccountOwner owner = Model.accountOwnerDao.get(ownerId);
        if (owner == null) {
            throw new APIError(
                String.format(
                    "Account owner with id \"%s\" does not exist",
                    ownerId
                ), 404
            );
        }
        if (balance < 0) {
            throw new APIError("Balance can not be negative", 400);
        }

        return Model.accountDao.create(owner, balance);
    }

    public static void transfer(String fromId, String toId, double amount) {
        Account from = getAccount(fromId);
        Account to = getAccount(toId);
        if (amount < 0) {
            throw new APIError("Amount can not be negative", 400);
        }
        if (from.getId().equals(to.getId())) {
            throw new APIError(
                "Can not transfer money from an account to itself",
                400
            );
        }

        try {
            Model.accountDao.transfer(from, to, amount);
        } catch (DatabaseAccountTransferError error) {
            throw new APIError(error.getMessage(), 400);
        }
    }

    public static void delete(String id) {
        Account deleted = Model.accountDao.delete(id);
        if (deleted == null) {
            throw new APIError(
                String.format("Account with id \"%s\" not found", id),
                404
            );
        }
    }

    private static Account getAccount(String id) {
        Account account = Model.accountDao.get(id);
        if (account == null) {
            throw new APIError(
                String.format(
                    "Account with id \"%s\" not found", id
                ), 404
            );
        }

        return account;
    }
}
