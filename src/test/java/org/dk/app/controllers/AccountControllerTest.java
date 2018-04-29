package org.dk.app.controllers;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

import junit.framework.TestCase;
import org.dk.app.APIError;
import org.dk.app.Model;
import org.dk.app.model.Account;
import org.dk.app.model.AccountOwner;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class AccountControllerTest extends TestCase {
    private AccountOwner testOwner;
    private AccountOwner auxOwner;

    @Before
    public void setUp() {
        Model.deleteAll();
        testOwner = AccountOwnerController.create(
            "test_name", "test_email@email.com"
        );
        auxOwner = AccountOwnerController.create(
            "aux_name", "aux_email@email.com"
        );
    }

    @Test
    public void testCreateAccount() {
        Account account = AccountController.create(testOwner.getId(), 100.0);
        assertThat(account.getOwnerId(), is(testOwner.getId()));
        assertThat(account.getBalance(), is(100.0));
    }

    @Test
    public void testCreateAccount_ownerNotFound() {
        try {
            AccountController.create("non-existing-id", 100.0);
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(404));
        }
    }

    @Test
    public void testCreateAccount_negativeBalance() {
        try {
            AccountController.create(testOwner.getId(), -0.5);
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(400));
        }
    }

    @Test
    public void testGetAccount() {
        Account account = AccountController.create(testOwner.getId(), 1.0);
        assertThat(AccountController.get(account.getId()), is(account));
    }

    @Test
    public void testGetAccount_notFound() {
        try {
            AccountController.get("non-existing-id");
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(404));
        }
    }

    @Test
    public void testTransfer() {
        Account from = AccountController.create(testOwner.getId(), 100);
        Account to = AccountController.create(auxOwner.getId(), 0);
        AccountController.transfer(from.getId(), to.getId(), 98.5);
        assertThat(from.getBalance(), is(1.5));
        assertThat(to.getBalance(), is(98.5));
    }

    @Test
    public void testTransfer_negativeBalance() {
        Account from = AccountController.create(testOwner.getId(), 100);
        Account to = AccountController.create(auxOwner.getId(), 100);

        try {
            AccountController.transfer(from.getId(), to.getId(), -2);
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(400));
        }
    }

    @Test
    public void testTransfer_insufficientBalance() {
        Account from = AccountController.create(testOwner.getId(), 10);
        Account to = AccountController.create(auxOwner.getId(), 100);

        try {
            AccountController.transfer(from.getId(), to.getId(), 10.5);
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(400));
        }
    }

    @Test
    public void testTransfer_fromIdNotFound() {
        Account from = AccountController.create(testOwner.getId(), 100);

        try {
            AccountController.transfer(from.getId(), "non-existing-id", 10);
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(404));
        }
    }

    @Test
    public void testTransfer_fromAccountToItself_shouldRaiseError() {
        Account from = AccountController.create(testOwner.getId(), 100);

        try {
            AccountController.transfer(from.getId(), from.getId(), 10);
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(400));
        }
    }

    @Test
    public void testTransfer_toIdNotFound() {
        Account to = AccountController.create(testOwner.getId(), 100);

        try {
            AccountController.transfer("non-existing-id", to.getId(), 10);
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(404));
        }
    }

    @Test
    public void testListAccounts() {
        Account first = AccountController.create(testOwner.getId(), 100);
        Account second = AccountController.create(auxOwner.getId(), 200);

        Collection<Account> accounts = AccountController.list();
        assertThat(accounts.size(), is(2));
        assertThat(accounts, containsInAnyOrder(first, second));
    }

    @Test
    public void testDeleteAccount() {
        Account account = AccountController.create(testOwner.getId(), 100);
        AccountController.create(testOwner.getId(), 200);

        assertThat(AccountController.list().size(), is(2));
        assertThat(
            AccountOwnerController.getAccounts(testOwner.getId()).size(),
            is(2)
        );

        AccountController.delete(account.getId());

        assertThat(AccountController.list().size(), is(1));
        Collection<Account> ownerAccounts = AccountOwnerController
            .getAccounts(testOwner.getId());

        assertThat(ownerAccounts.size(), is(1));
        assertThat(ownerAccounts, not(contains(account)));
    }

    @Test
    public void testDeleteAccount_notFound() {
        try {
            AccountController.delete("non-existing-id");
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(404));
        }
    }
}