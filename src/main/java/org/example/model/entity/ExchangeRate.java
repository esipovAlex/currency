package org.example.model.entity;

import java.math.BigDecimal;

public record ExchangeRate(int id, int baseCurrentId, int targetCurrencyId, BigDecimal rate) {
}
