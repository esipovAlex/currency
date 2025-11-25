package org.example.dao.commands;

import org.example.model.entity.Currency;
import org.example.enums.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaveCurrencyCommand extends BaseCommand<Currency> {
    private final Currency currency;

    public SaveCurrencyCommand(Currency currency) {
        this.currency = currency;
    }

    @Override
    protected Currency doExecute(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                Queries.INSERT_INTO_CURR.getStmnt(), PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, currency.code());
            ps.setString(2, currency.fullName());
            ps.setString(3, currency.sign());
            ps.execute();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Currency(rs.getInt(1), currency.code(), currency.fullName(), currency.sign());
                }
            }
        }
        throw new SQLException("Failed to save currency");
    }
}