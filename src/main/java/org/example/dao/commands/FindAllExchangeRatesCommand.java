package org.example.dao.commands;

import org.example.mapper.CreateMapper;
import org.example.model.response.ExRateRespDto;
import org.example.enums.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FindAllExchangeRatesCommand extends BaseCommand<List<ExRateRespDto>> {

    @Override
    protected List<ExRateRespDto> doExecute(Connection connection) throws SQLException {
        List<ExRateRespDto> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(Queries.SELECT_ALL_RATES.getStmnt());
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                result.add(CreateMapper.create(resultSet));
            }
        }
        return result;
    }
}
