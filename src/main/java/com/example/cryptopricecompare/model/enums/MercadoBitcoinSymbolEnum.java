package com.example.cryptopricecompare.model.enums;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MercadoBitcoinSymbolEnum {

    BTC("BTC"),
    ETH("ETH"),
    LTC("LTC"),
    USD("USD"),
    EUR("EUR"),
    BRL("BRL");

    private final String value;

    public static String valueOf(BaseSymbol baseSymbol) {
        return valueOf(baseSymbol.name()).getValue();
    }

    public static String valueOf(QuoteSymbol quoteSymbol) {
        return valueOf(quoteSymbol.name()).getValue();
    }

    public static MercadoBitcoinSymbolEnum mercadoBitcoinSymbolOf(BaseSymbol baseSymbol) {
        return valueOf(baseSymbol.name());
    }

    public static MercadoBitcoinSymbolEnum mercadoBitcoinSymbolOf(QuoteSymbol quoteSymbol) {
        return valueOf(quoteSymbol.name());
    }

    public static boolean hasSymbol(BaseSymbol baseSymbol) {
        return Arrays.stream(values())
                .anyMatch(mercadoBitcoinSymbolEnum -> mercadoBitcoinSymbolEnum.name().equals(baseSymbol.name()));
    }

    public static boolean hasSymbol(QuoteSymbol quoteSymbol) {
        return Arrays.stream(values())
                .anyMatch(mercadoBitcoinSymbolEnum -> mercadoBitcoinSymbolEnum.name().equals(quoteSymbol.name()));
    }

}
