package org.example.dao.commands;

import org.example.mapper.CreateMapper;
import org.example.model.request.ExcRateCode;
import org.example.model.response.ExRateRespDto;
import org.example.enums.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindByCodesCommand extends BaseCommand<ExRateRespDto> {
    private final ExcRateCode exchangeRate;

    public FindByCodesCommand(ExcRateCode exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    protected ExRateRespDto doExecute(Connection connection) throws SQLException {
        try (PreparedStatement psSelect = connection.prepareStatement(Queries.SELECT_RATES_BY_CODES.getStmnt())) {
            psSelect.setString(1, exchangeRate.base());
            psSelect.setString(2, exchangeRate.target());

            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    return CreateMapper.create(rs);
                }
            }
        }
        throw new SQLException("Currency pair not found");
    }
}
