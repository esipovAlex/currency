package org.example.service;

import org.example.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.regex.Pattern;

import static org.example.exceptionhandler.MessageErr.*;

public class ValidService {

    private static final Logger LOG = LoggerFactory.getLogger(ValidService.class.getName());

    protected void isValidCodes(String patchInfo) {
        if (Objects.isNull(patchInfo)
                || !patchInfo.startsWith("/")) {
            throw new CodeCurrencyAbsentException(CODE_CURRENCY_ABSENT.getMessage());
        }
        String codeRegex = "^/[A-Z]{6}$";
        Pattern pattern = Pattern.compile(codeRegex);
        if (!pattern.matcher(patchInfo).matches()) {
            LOG.error("Код валюты не соответствует формату {}", patchInfo);
            throw new CodeCurrenciesException(CODE_FORMAT_INVALID.getMessage());
        }
    }

    protected void isValidForm(String form) {
        if (form.isBlank()) {
            LOG.error("Форма не передана");
            throw new FormException(FORM_NOT_EXIST.getMessage());
        }
        if (form.contains("&")) {
            LOG.error("Форма имеет неверную структуру {}", form);
            throw new FieldFormException(FORM_FORMAT_INVALID.getMessage());
        }
        if (!form.startsWith("rate=")) {
            LOG.error("Отсутствует нужное поле формы {}", form);
            throw new FieldFormException(FIELD_FORM_ABSENT.getMessage());
        }
    }

    protected void isValidRate(String rate) {
        String rateRegex = "^\\d+(?:\\.\\d+)?$";
        Pattern pattern = Pattern.compile(rateRegex);
        if (Objects.isNull(rate) || !pattern.matcher(rate).matches()) {
            LOG.error("Обменный курс не соответствует формату {}", rate);
            throw new RateParameterException(RATE_FORMAT_INVALID.getMessage());
        }
    }

    protected void isEmpty(String pathInfo) {
        if (Objects.nonNull(pathInfo) && !"/".equals(pathInfo)) {
            LOG.error("Неверный путь к ресурсу {}", pathInfo);
            throw new PathVariableException(PATH_INVALID.getMessage());
        }
    }

    protected void isValidCode(String code) {
        String codeRegex = "^[A-Z]{3}$";
        Pattern pattern = Pattern.compile(codeRegex);
        if (Objects.isNull(code) || !pattern.matcher(code).matches()) {
            LOG.error("Код валюты не соответствует формату {}", code);
            throw new CodeCurrenciesException(CODE_FORMAT_INVALID.getMessage());
        }
    }
}
