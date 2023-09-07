package com.example.cryptopricecompare.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuoteSymbol {

    USD("USD"),
    EUR("EUR"),
    BRL("BRL"),
    UNSUPPORTED("UNSUPPORTED");

    private final String value;

}
