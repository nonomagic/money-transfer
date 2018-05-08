package org.dk.app.model.dao;

import org.dk.app.model.Account;
import org.dk.app.model.AccountOwner;
import org.dk.app.model.dao.exceptions.DatabaseAccountTransferError;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class AccountDaoInmem implements AccountDao {
    private static SortedMap<String, Account> idMap = new TreeMap<>();
    private static Map<String, LinkedList<Account>> ownerMap = new HashMap<>();

    @Override
    public Account create(AccountOwner owner, double balance) {
        String accountId = UUID.randomUUID().toString();
        Account account = new Account(accountId, balance, owner.getId());

        synchronized (this) {
            idMap.put(accountId, account);

            List<Account> ownerAccounts = ownerMap.computeIfAbsent(
                owner.getId(), id -> new LinkedList<>()
            );
            ownerAccounts.add(account);
        }

        return account;
    }

    @Override
    public Collection<Account> list() {
        synchronized (this) {
            return idMap.values();
        }
    }

    @Override
    public Collection<Account> listByOwner(String ownerId) {
        Collection<Account> accounts;
        synchronized (this) {
            accounts = ownerMap.get(ownerId);
        }
        if (accounts == null) {
            return Collections.EMPTY_LIST;
        }

        return accounts;
    }

    @Override
    public Account get(String id) {
        return idMap.get(id);
    }

    @Override
    public Account delete(String id) {
        Account deleted;
        synchronized (this) {
            deleted = idMap.remove(id);
            if (deleted != null) {
                LinkedList<Account> ownerAccounts = ownerMap.get(deleted.getOwnerId());
                if (ownerAccounts != null) {
                    // An assumption is made that an owner will rarely
                    // have hundreds or thousands of accounts, so storing
                    // them in a list should be fine even though remove
                    // operation is O(n).
                    ownerAccounts.remove(deleted);
                }
            }
        }

        return deleted;
    }

    @Override
    public void transfer(Account from, Account to, double amount) {
        synchronized (this) {
            if (from.getBalance() - amount < 0) {
                throw new DatabaseAccountTransferError(
                    String.format(
                        "Not enough money on balance of \"%s\" to complete the transfer",
                        from.getId()
                    )
                );
            }

            to.setBalance(to.getBalance() + amount);
            from.setBalance(from.getBalance() - amount);
        }
    }

    @Override
    public void deleteAll() {
        synchronized (this) {
            idMap.clear();
            ownerMap.clear();
        }
    }
}
