package org.example.model.request;

import java.math.BigDecimal;

public record ExchCreateRequest(String base, String target, BigDecimal rate) {
}
