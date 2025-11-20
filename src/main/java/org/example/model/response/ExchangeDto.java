package org.example.model.response;

import java.math.BigDecimal;

public record ExchangeDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
}
