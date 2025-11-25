package org.example.dao.commands;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseCommand<T> {
     T execute(Connection connection) throws SQLException;
}
