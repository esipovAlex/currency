package org.example.mapper;

import org.example.model.entity.Currency;
import org.example.model.response.CurrencyDto;
import org.example.model.response.ExRateRespDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateMapper {

    private CreateMapper() {
    }

    public static ExRateRespDto create(ResultSet rs) throws SQLException {
        return new ExRateRespDto(
                rs.getInt("id"),
                new CurrencyDto(
                        rs.getInt("base_current_id"),
                        rs.getString("base_code"),
                        rs.getString("base_full_name"),
                        rs.getString("base_sign")
                ),
                new CurrencyDto(
                        rs.getInt("target_current_id"),
                        rs.getString("target_code"),
                        rs.getString("target_full_name"),
                        rs.getString("target_sign")
                ),
                rs.getBigDecimal("rate")
        );
    }

    public static Currency createCurrency(ResultSet rs) throws SQLException {
        return new Currency(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("full_name"),
                rs.getString("sign")
        );
    }
}
