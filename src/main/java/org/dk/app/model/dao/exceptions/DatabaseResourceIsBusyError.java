package org.dk.app.model.dao.exceptions;

public class DatabaseResourceIsBusyError extends DatabaseError {
    private String resourceId;

    public DatabaseResourceIsBusyError(String resourceId) {
        super(String.format("Resource \"%s\" is budy", resourceId));
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }
}
