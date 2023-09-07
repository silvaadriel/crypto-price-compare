package com.example.cryptopricecompare.model.dto;

import com.example.cryptopricecompare.model.Error;
import org.apache.logging.log4j.util.Strings;

public class CryptoPriceCompareErrorDTO extends Error {

    public CryptoPriceCompareErrorDTO(final String code, final String message) {
        super();
        this.code(code).description(message);
    }

    public CryptoPriceCompareErrorDTO(final String code, final String message, final String messageDetail) {
        super();
        final String formattedMessage = Strings.isNotBlank(messageDetail) ? String.format("%s [%s]", message, messageDetail) : message;
        this.code(code).description(formattedMessage);
    }

}
