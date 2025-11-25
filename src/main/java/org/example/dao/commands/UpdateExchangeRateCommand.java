package org.example.dao.commands;

import org.example.mapper.CreateMapper;
import org.example.model.request.ExchCreateRequest;
import org.example.model.response.ExRateRespDto;
import org.example.constants.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateExchangeRateCommand extends BaseCommand<ExRateRespDto> {
    private final ExchCreateRequest exchangeRate;

    public UpdateExchangeRateCommand(ExchCreateRequest exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    protected ExRateRespDto doExecute(Connection connection) throws SQLException {
        try (PreparedStatement psUpdate = connection.prepareStatement(Queries.UPDATE_RATES.getStmnt())) {
            psUpdate.setBigDecimal(1, exchangeRate.rate());
            psUpdate.setString(2, exchangeRate.base());
            psUpdate.setString(3, exchangeRate.target());
            int affectedRows = psUpdate.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Currency pair not found");
            }
        }
        try (PreparedStatement psSelect = connection.prepareStatement(Queries.SELECT_FROM_RATES.getStmnt())) {
            psSelect.setString(1, exchangeRate.base());
            psSelect.setString(2, exchangeRate.target());
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    return  CreateMapper.create(rs);
                }
            }
        }
        throw new SQLException("Failed to retrieve updated exchange rate");
    }
}