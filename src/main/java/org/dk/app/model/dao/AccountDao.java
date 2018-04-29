package org.dk.app.model.dao;

import org.dk.app.model.Account;
import org.dk.app.model.AccountOwner;

import java.util.Collection;

public interface AccountDao {
    Account create(AccountOwner owner, double balance);
    Collection<Account> list();
    Collection<Account> listByOwner(String ownerId);
    Account get(String id);
    Account delete(String id);

    /*
     * Note: transfer operation logic has always to be implemented in DAO
     * layer, because it depends on the implementation details of the
     * atomicity layer the underlying database support.
     */
    void transfer(Account from, Account to, double amount);
    void deleteAll();
}
