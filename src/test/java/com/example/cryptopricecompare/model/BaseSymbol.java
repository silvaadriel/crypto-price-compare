package com.example.cryptopricecompare.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseSymbol {

    BTC("BTC"),
    ETH("ETH"),
    LTC("LTC"),
    UNSUPPORTED("UNSUPPORTED");

    private final String value;

}
