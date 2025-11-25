package org.example.constants;


import org.example.model.response.AllCurrencyDto;

import java.util.Arrays;
import java.util.List;

public enum AllCurrency {

    AUD("AUD", "Австралийский доллар", "A$"),
    AZN("AZN", "Азербайджанский манат", "₼"),
    DZD("DZD", "Алжирский динар", "DZD"),
    AMD("AMD", "Армянский драм", "֏"),
    THB("THB", "Бат", "฿"),
    BHD("BHD", "Бахрейнский динар", "BHD"),
    BYN("BYN", "Белорусский рубль", "Br"),
    BGN("BGN", "Болгарский лев", "лв"),
    BOB("BOB", "Боливиано", "Bs"),
    BRL("BRL", "Бразильский реал", "R$"),
    KRW("KRW", "Вон", "₩"),
    HKD("HKD", "Гонконгский доллар", "HK$"),
    UAH("UAH", "Гривна", "₴"),
    DKK("DKK", "Датская крона", "kr"),
    AED("AED", "Дирхам ОАЭ", "AED"),
    USD("USD", "Доллар США", "$"),
    VND("VND", "Донг", "₫"),
    EUR("EUR", "Евро", "€"),
    EGP("EGP", "Египетский фунт", "EGP"),
    PLN("PLN", "Злотый", "zł"),
    JPY("JPY", "Иена", "¥"),
    INR("INR", "Индийская рупия", "₹"),
    IRR("IRR", "Иранский риал", "IRR"),
    CAD("CAD", "Канадский доллар", "C$"),
    QAR("QAR", "Катарский риал", "QAR"),
    CUP("CUP", "Кубинских песо", "$"),
    MMK("MMK", "Кьят", "K"),
    GEL("GEL", "Лари", "ლ"),
    MDL("MDL", "Молдавский лей", "L"),
    NGN("NGN", "Найр", "₦"),
    NZD("NZD", "Новозеландский доллар", "NZ$"),
    TMT("TMT", "Новый туркменский манат", "T"),
    NOK("NOK", "Норвежская крона", "kr"),
    OMR("OMR", "Оманский риал", "OMR"),
    RON("RON", "Румынский лей", "lei"),
    IDR("IDR", "Рупия", "Rp"),
    ZAR("ZAR", "Рэнд", "R"),
    SAR("SAR", "Саудовский риял", "SAR"),
    XDR("XDR", "СДР (специальные права заимствования)", "XDR"),
    RSD("RSD", "Сербский динар", "RSD"),
    SGD("SGD", "Сингапурский доллар", "S$"),
    KGS("KGS", "Сом", "som"),
    TJS("TJS", "Сомони", "somoni"),
    BDT("BDT", "Так", "৳"),
    KZT("KZT", "Тенге", "₸"),
    MNT("MNT", "Тугрик", "₮"),
    TRY("TRY", "Турецкая лира", "₺"),
    UZS("UZS", "Узбекский сум", "UZS"),
    HUF("HUF", "Форинт", "Ft"),
    GBP("GBP", "Фунт стерлингов", "£"),
    CZK("CZK", "Чешская крона", "Kč"),
    SEK("SEK", "Шведская крона", "kr"),
    CHF("CHF", "Швейцарский франк", "Fr"),
    ETB("ETB", "Эфиопский быр", "Br"),
    CNY("CNY", "Юань", "¥");

    private static final List<AllCurrencyDto> CURRENCY_DTOS = Arrays.stream(values())
            .map(el -> new AllCurrencyDto(el.code, el.name, el.symbol))
            .toList();
    private final String code;
    private final String name;
    private final String symbol;

    AllCurrency(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public static List<AllCurrencyDto> dtos() {
        return CURRENCY_DTOS;
    }
}
