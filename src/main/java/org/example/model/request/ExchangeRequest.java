package org.example.model.request;

import java.math.BigDecimal;

public record ExchangeRequest(String base, String target, BigDecimal amount) {
}
