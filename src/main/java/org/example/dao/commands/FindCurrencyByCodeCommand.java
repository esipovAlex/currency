package org.example.dao.commands;

import org.example.mapper.CreateMapper;
import org.example.model.entity.Currency;
import org.example.constants.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindCurrencyByCodeCommand extends BaseCommand<Currency> {
    private final String code;

    public FindCurrencyByCodeCommand(String code) {
        this.code = code;
    }

    @Override
    protected Currency doExecute(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(Queries.SELECT_CURR_BY_CODE.getStmnt())) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return CreateMapper.createCurrency(rs);
                }
            }
        }
        return null;
    }
}