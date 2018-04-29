package org.dk.app.model.dao.exceptions;

public class DatabaseResorceAlreadyExistsError extends DatabaseError {
    public DatabaseResorceAlreadyExistsError(String resourceName) {
        super(String.format("Resource \"%s\" already exists", resourceName));
    }
}
