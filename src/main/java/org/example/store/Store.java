package org.example.store;

import org.example.model.entity.Currency;
import org.example.model.request.ExchCreateRequest;
import org.example.model.response.ExRateRespDto;
import org.example.model.request.ExcRateCode;

import java.util.List;

public interface Store {

    Currency save(Currency currency);

    Currency findByCode(String code);

    List<Currency> findAllCurrencies();

    ExRateRespDto save(ExchCreateRequest exchangeRate);

    ExRateRespDto update(ExchCreateRequest exchangeRate);

    ExRateRespDto findByCodes(ExcRateCode exchangeRate);

    List<ExRateRespDto> findAllExRate();
}
