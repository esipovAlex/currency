package org.example.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.example.dao.commands.DatabaseCommand;
import org.example.exception.CurrencyAlreadyExistsException;
import org.example.exception.CurrencyNotFoundException;
import org.example.exception.DataBaseErrorException;

import java.sql.Connection;
import java.sql.SQLException;

import static org.example.exceptionhandler.MessageErr.*;

public class CommandExecutor {
    private final BasicDataSource dataSource;

    public CommandExecutor(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T execute(DatabaseCommand<T> command) {
        try (Connection connection = dataSource.getConnection()) {
            return command.execute(connection);
        } catch (SQLException e) {
            throw convertException(e);
        }
    }

    public <T> T executeTransaction(DatabaseCommand<T> command) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            T result = command.execute(connection);
            connection.commit();
            return result;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    // log rollback error
                }
            }
            throw convertException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    // log close error
                }
            }
        }
    }

    private RuntimeException convertException(SQLException e) {
        String message = e.getMessage();
        if (message != null) {
            if (message.contains("[SQLITE_CONSTRAINT_UNIQUE]")) {
                if (message.contains("currency")) {
                    return new CurrencyAlreadyExistsException(CURRENCY_ALREADY_EXIST.getMessage());
                } else {
                    return new CurrencyAlreadyExistsException(CURRENCY_PAIR_ALREADY_EXIST.getMessage());
                }
            }
            if (message.contains("not found") || message.contains("does not exist")) {
                return new CurrencyNotFoundException(CURRENCY_PAIR_NOT_EXIST.getMessage());
            }
        }
        return new DataBaseErrorException(DATABASE_ERROR.getMessage());
    }
}
