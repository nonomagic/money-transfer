package org.dk.app.model;

import org.apache.commons.lang.builder.EqualsBuilder;

public class AccountOwner {
    private String id;
    private String name;
    private String email;

    public AccountOwner(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof  AccountOwner)) {
            return false;
        }

        AccountOwner other = (AccountOwner) obj;
        return new EqualsBuilder()
            .append(id, other.id)
            .append(name, other.name)
            .append(email, other.email)
            .isEquals();
    }
}
