package org.example.dao.commands;

import org.example.mapper.CreateMapper;
import org.example.model.entity.Currency;
import org.example.constants.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FindAllCurrenciesCommand extends BaseCommand<List<Currency>> {
    @Override
    protected List<Currency> doExecute(Connection connection) throws SQLException {
        List<Currency> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(Queries.SELECT_CURR.getStmnt());
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                result.add(CreateMapper.createCurrency(resultSet));
            }
        }
        return result;
    }
}
