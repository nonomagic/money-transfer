package org.dk.app.model.dao;

import org.dk.app.model.AccountOwner;

import java.util.Collection;

public interface AccountOwnerDao {
    AccountOwner create(String name, String email);
    Collection<AccountOwner> list();
    AccountOwner get(String id);
    AccountOwner delete(String id);
    void deleteAll();
}
