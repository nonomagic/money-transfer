package org.dk.app.model.dao;

import org.dk.app.Model;
import org.dk.app.model.Account;
import org.dk.app.model.AccountOwner;
import org.dk.app.model.dao.exceptions.DatabaseResorceAlreadyExistsError;
import org.dk.app.model.dao.exceptions.DatabaseResourceIsBusyError;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class AccountOwnerDaoInmem implements AccountOwnerDao {
    private Map<String, AccountOwner> emailMap = new HashMap<>();
    private SortedMap<String, AccountOwner> idMap = new TreeMap<>();

    @Override
    public AccountOwner create(String name, String email) {
        String id = UUID.randomUUID().toString();
        AccountOwner owner = new AccountOwner(id, name, email);
        if (emailMap.get(email) != null) {
            throw new DatabaseResorceAlreadyExistsError(email);
        }

        emailMap.put(owner.getEmail(), owner);
        idMap.put(owner.getId(), owner);
        return owner;
    }

    @Override
    public Collection<AccountOwner> list() {
        return idMap.values();
    }

    @Override
    public AccountOwner get(String id) {
        return idMap.get(id);
    }
    
    @Override
    public AccountOwner delete(String id) {
        Collection<Account> accounts = Model.accountDao.listByOwner(id);
        if (accounts != null && accounts.size() > 0) {
            throw new DatabaseResourceIsBusyError(id);
        }
        AccountOwner owner = idMap.remove(id);
        if (owner != null) {
            emailMap.remove(owner.getEmail());
        }

        return owner;
    }

    @Override
    public void deleteAll() {
        emailMap.clear();
        idMap.clear();
    }
}
