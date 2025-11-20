package org.example.model.response;

import java.math.BigDecimal;

public record ExRateRespDto(int id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate) {
}
