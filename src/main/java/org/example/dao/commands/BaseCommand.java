package org.example.dao.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseCommand<T> implements DatabaseCommand<T> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected abstract T doExecute(Connection connection) throws SQLException;

    @Override
    public T execute(Connection connection) throws SQLException {
        try {
            return doExecute(connection);
        } catch (SQLException e) {
            log.error("Database error in command: {}", getClass().getSimpleName(), e);
            throw e;
        }
    }
}
