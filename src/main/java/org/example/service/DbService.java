package org.example.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.dao.DbStore;
import org.example.exception.CurrencyNotFoundException;
import org.example.mapper.CurrencyMapper;
import org.example.model.entity.Currency;
import org.example.model.request.ExcRateCode;
import org.example.model.request.ExchCreateRequest;
import org.example.model.response.CurrencyDto;
import org.example.model.response.ExRateRespDto;
import org.example.model.response.ExchangeDto;
import org.example.dao.Store;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static org.example.exceptionhandler.MessageErr.CURRENCY_NOT_FOUND;

public class DbService {
    private static final Logger LOG = LoggerFactory.getLogger(DbService.class.getName());

    private static final Gson GSON = new GsonBuilder().create();
    private final Store store;
    private final ValidService validService;
    private final CurrencyMapper mapper;

    public DbService() {
        this.validService = new ValidService();
        this.store = DbStore.instOf();
        this.mapper = Mappers.getMapper(CurrencyMapper.class);
    }

    public String save(String pathInfo, String name, String code, String sign) {
        validService.isEmpty(pathInfo);
        Currency newCurrency = new Currency(0, name, code, sign);
        return GSON.toJson(mapper.toDto(store.save(newCurrency)));
    }

    public String getCurrency(String pathInfo) {
        String json = "";
        if (Objects.isNull(pathInfo) || "/".equals(pathInfo)) {
            List<CurrencyDto> currencies = allCurrency();
            json = GSON.toJson(currencies);
        } else {
            String code = pathInfo.substring(1);
            validService.isValidCode(code);
            CurrencyDto currency = findByCode(code);
            json = GSON.toJson(currency);
        }
        return json;
    }

    private List<CurrencyDto> allCurrency() {
        return  mapper.toDtoList(store.findAllCurrencies());
    }

    private CurrencyDto findByCode(String code) {
        return mapper.toDto(store.findByCode(code));
    }

    public String findByCodes(String pathInfo) {
        validService.isValidCodes(pathInfo);
        ExRateRespDto exRateRespDto = store.findByCodes(
                new ExcRateCode(
                        pathInfo.substring(1, 4),
                        pathInfo.substring(4)
                ));
        return GSON.toJson(exRateRespDto);
    }

    public String save(String base, String target, String rateRaw) {
        validService.isValidCode(base);
        validService.isValidCode(target);
        validService.isValidRate(rateRaw);
        return GSON.toJson(store.save(
                new ExchCreateRequest(
                        base,
                        target,
                        BigDecimal.valueOf(Double.valueOf(rateRaw))
        )));
    }

    public String update(String pathInfo, String rawForm) {
        validService.isValidCodes(pathInfo);
        validService.isValidForm(rawForm);
        String rateRaw = rawForm.substring(5);
        validService.isValidRate(rateRaw);
        ExRateRespDto exRateRespDto = store.update(
                new ExchCreateRequest(
                        pathInfo.substring(1, 4),
                        pathInfo.substring(4),
                        BigDecimal.valueOf(Double.valueOf(rateRaw))
        ));
        return GSON.toJson(exRateRespDto);
    }

    public String findAllExRate() {
        return GSON.toJson(store.findAllExRate());
    }

    public String exchange(String base, String target, String amountRaw) {
        LOG.info("внутри метода exchange");
        validService.isValidCode(base);
        validService.isValidCode(target);
        validService.isValidRate(amountRaw);
        BigDecimal amount = BigDecimal.valueOf(Double.valueOf(amountRaw));
        ExRateRespDto basTar = store.findByCodes(new ExcRateCode(base, target));
        if (Objects.nonNull(basTar)) {
            return GSON.toJson(
                    createExchange(
                        basTar.baseCurrency(),
                        basTar.targetCurrency(),
                        basTar.rate(),
                        amount));
        }
        ExRateRespDto tarBas = store.findByCodes(new ExcRateCode(target, base));
        if (Objects.nonNull(tarBas)) {
            return GSON.toJson(
                    createExchange(
                        tarBas.targetCurrency(),
                        tarBas.baseCurrency(),
                        BigDecimal.ONE.divide(tarBas.rate(), 2, RoundingMode.HALF_EVEN),
                        amount));
        }
        ExRateRespDto usdTar = store.findByCodes(new ExcRateCode("USD", target));
        ExRateRespDto usdBas = store.findByCodes(new ExcRateCode("USD", base));
        if (Objects.isNull(usdTar) || Objects.isNull(usdBas)) {
            throw new CurrencyNotFoundException(CURRENCY_NOT_FOUND.getMessage());
        }
        return GSON.toJson(
                createExchange(
                    usdBas.targetCurrency(),
                    usdTar.targetCurrency(),
                    usdBas.rate().divide(usdTar.rate(), 2, RoundingMode.HALF_EVEN),
                    amount));
    }

    private ExchangeDto createExchange(CurrencyDto baseCurr, CurrencyDto targetCurr, BigDecimal rate, BigDecimal amount) {
        return new ExchangeDto(
                baseCurr,
                targetCurr,
                rate,
                amount,
                rate.multiply(amount).setScale(2, RoundingMode.HALF_EVEN));
    }
}

