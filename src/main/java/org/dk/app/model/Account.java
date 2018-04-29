package org.dk.app.model;

import org.apache.commons.lang.builder.EqualsBuilder;

public class Account {
    private String id;
    private String ownerId;
    private double balance;

    public Account(String id, double balance, String ownerId) {
        this.id = id;
        this.balance = balance;
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account)) {
            return false;
        }

        Account other = (Account) obj;
        return new EqualsBuilder()
            .append(id, other.id)
            .append(balance, other.balance)
            .append(ownerId, other.ownerId)
            .isEquals();
    }
}
