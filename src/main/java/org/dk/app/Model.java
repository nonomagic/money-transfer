package org.dk.app;

import org.dk.app.model.dao.AccountDao;
import org.dk.app.model.dao.AccountDaoInmem;
import org.dk.app.model.dao.AccountOwnerDao;
import org.dk.app.model.dao.AccountOwnerDaoInmem;

public class Model {
    public static AccountOwnerDao accountOwnerDao = new AccountOwnerDaoInmem();
    public static AccountDao accountDao = new AccountDaoInmem();

    public static void deleteAll() {
        accountOwnerDao.deleteAll();
        accountDao.deleteAll();
    }
}
