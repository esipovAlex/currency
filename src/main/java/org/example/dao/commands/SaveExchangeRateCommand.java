package org.example.dao.commands;

import org.example.mapper.CreateMapper;
import org.example.model.request.ExchCreateRequest;
import org.example.model.response.ExRateRespDto;
import org.example.enums.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaveExchangeRateCommand extends BaseCommand<ExRateRespDto> {
    private final ExchCreateRequest exchangeRate;

    public SaveExchangeRateCommand(ExchCreateRequest exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    protected ExRateRespDto doExecute(Connection connection) throws SQLException {
        try (PreparedStatement psInsert = connection.prepareStatement(Queries.INSERT_INTO_RATES.getStmnt())) {
            psInsert.setBigDecimal(1, exchangeRate.rate());
            psInsert.setString(2, exchangeRate.base());
            psInsert.setString(3, exchangeRate.target());
            int affectedRows = psInsert.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Currency pair does not exist");
            }
        }

        try (PreparedStatement psSelect = connection.prepareStatement(Queries.SELECT_FROM_RATES_LAST.getStmnt());
             ResultSet rs = psSelect.executeQuery()) {
            if (rs.next()) {
                return CreateMapper.create(rs);
            }
        }
        throw new SQLException("Failed to retrieve saved exchange rate");
    }
}