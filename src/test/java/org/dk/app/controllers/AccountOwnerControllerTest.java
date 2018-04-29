package org.dk.app.controllers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import junit.framework.TestCase;
import org.dk.app.APIError;
import org.dk.app.Model;
import org.dk.app.model.Account;
import org.dk.app.model.AccountOwner;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class AccountOwnerControllerTest extends TestCase {
    @Before
    public void setUp() {
        Model.deleteAll();
    }

    @Test
    public void testCreateAccountOwner() {
        AccountOwner owner = AccountOwnerController.create(
            "name", "email@email.com"
        );

        assertThat(owner.getName(), is("name"));
        assertThat(owner.getEmail(), is("email@email.com"));
    }

    @Test
    public void testCreateAccountIdUniqueness() {
        AccountOwner owner1 = AccountOwnerController.create(
            "name1", "email1@email.com"
        );
        AccountOwner owner2 = AccountOwnerController.create(
            "name2", "email2@email.com"
        );

        assertNotSame(owner1.getId(), owner2.getId());
    }

    @Test
    public void testAccountOwnerEmptyName() {
        try {
            AccountOwner owner = AccountOwnerController.create("", "email@email.com");
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(400));
        }
    }

    @Test
    public void testAccountOwnerEmptyEmail() {
        try {
            AccountOwner owner = AccountOwnerController.create("name", "");
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(400));
        }
    }

    @Test
    public void testAccountOnwnerEmailsShouldBeUnique() {
        AccountOwner owner = AccountOwnerController.create("name", "test@email.com");
        try {
            AccountOwnerController.create("name2", "test@email.com");
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(409));
        }
    }

    @Test
    public void testListAccountOwners() {
        AccountOwner owner1 = AccountOwnerController.create("name1", "emai1@email.com");
        AccountOwner owner2 = AccountOwnerController.create("name2", "emai2@email.com");
        Collection<AccountOwner> owners = new ArrayList<>(AccountOwnerController.list());

        assertThat(owners.size(), is(2));
        assertThat(owners, containsInAnyOrder(owner1, owner2));
    }

    @Test
    public void testGetAccounts() {
        AccountOwner owner = AccountOwnerController.create("name", "email@email.com");
        AccountOwner otherOwner = AccountOwnerController.create("name2", "email2@email.com");

        Account account1 = AccountController.create(owner.getId(), 100);
        Account account2 = AccountController.create(owner.getId(), 0);
        AccountController.create(otherOwner.getId(), 10);

        Collection<Account> accounts = AccountOwnerController.getAccounts(owner.getId());
        assertThat(accounts.size(), is(2));
        assertThat(accounts, containsInAnyOrder(account1, account2));
    }

    @Test
    public void testGetAccount_nonExistingId() {
        try {
            AccountOwnerController.getAccounts("non-existing-id");
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(404));
        }
    }

    @Test
    public void testDeleteAccountOwner() {
        AccountOwner owner = AccountOwnerController.create("name", "email@email.com");
        assertThat(AccountOwnerController.list().size(), is(1));

        AccountOwnerController.delete(owner.getId());
        assertThat(AccountOwnerController.list().size(), is(0));
    }

    @Test
    public void testDeleteAccountOwner_withInvalidId_shouldRaiseAnException() {
        try {
            AccountOwnerController.delete("non-existing-id");
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(404));
        }
    }

    @Test
    public void testDeleteAccountOwner_shouldRaiseError_whenHasAccounts() {
        AccountOwner owner = AccountOwnerController.create("name", "email@emai.com");
        AccountController.create(owner.getId(), 100);

        try {
            AccountOwnerController.delete(owner.getId());
            fail();
        } catch (APIError error) {
            assertThat(error.getErrorCode(), is(400));
        }
    }
}