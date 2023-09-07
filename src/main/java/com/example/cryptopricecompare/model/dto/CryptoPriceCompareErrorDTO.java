package com.example.cryptopricecompare.model.dto;

import com.example.cryptopricecompare.model.Error;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

public class CryptoPriceCompareErrorDTO extends Error {

    public CryptoPriceCompareErrorDTO(final String code, final String message) {
        super();
        this.code(code).description(message);
    }

    public CryptoPriceCompareErrorDTO(final String code, final String message, final String messageDetail) {
        super();
        final String formattedMessage = isNotBlank(messageDetail) ? "%s [%s]".formatted(message, messageDetail) : message;
        this.code(code).description(formattedMessage);
    }

}
