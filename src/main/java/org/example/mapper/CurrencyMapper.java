package org.example.mapper;

import org.example.model.entity.Currency;
import org.example.model.response.CurrencyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface CurrencyMapper {

    @Mapping(target = "name", source = "fullName")
    CurrencyDto toDto(Currency currency);

    List<CurrencyDto> toDtoList(List<Currency> currencies);
}

