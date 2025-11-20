package org.example.exceptionhandler;

public enum MessageErr {
    CURRENCY_NOT_FOUND("Валюта не найдена"),
    CURRENCY_ALREADY_EXIST("Валюта с таким кодом уже существует"),
    CURRENCY_PAIR_ALREADY_EXIST("Валютная пара с таким кодом уже существует"),
    CURRENCY_PAIR_NOT_EXIST("Одна (или обе) валюта из валютной пары не существует в БД"),
    DATABASE_ERROR("Ошибка базы данных "),
    CODE_CURRENCY_ABSENT("Коды валют не переданы "),
    CODE_FORMAT_INVALID("Код валюты не соответствует формату"),
    RATE_FORMAT_INVALID("Обменный курс не соответствует формату"),
    FORM_NOT_EXIST("Форма не передана"),
    PATH_INVALID("Неверный путь к ресурсу"),
    FORM_FORMAT_INVALID("Форма имеет неверную структуру"),
    FIELD_FORM_ABSENT("Отсутствует нужное поле формы");
    private final String message;

    public String getMessage() {
        return message;
    }

    MessageErr(String message) {
        this.message = message;
    }
}
