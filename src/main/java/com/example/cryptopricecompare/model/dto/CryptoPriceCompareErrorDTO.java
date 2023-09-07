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
        final String detalheMensagem = Strings.isNotBlank(messageDetail) ? String.format(" [%s]", messageDetail) : null;
        this.code(code).description(message + detalheMensagem);
    }

}
