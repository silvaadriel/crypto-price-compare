package com.example.cryptopricecompare.model.enums;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        return MercadoBitcoinSymbolEnum.valueOf(baseSymbol.name()).getValue();
    }

    public static String valueOf(QuoteSymbol quoteSymbol) {
        return MercadoBitcoinSymbolEnum.valueOf(quoteSymbol.name()).getValue();
    }

    public static MercadoBitcoinSymbolEnum mercadoBitcoinSymbolOf(BaseSymbol baseSymbol) {
        return MercadoBitcoinSymbolEnum.valueOf(baseSymbol.name());
    }

    public static MercadoBitcoinSymbolEnum mercadoBitcoinSymbolOf(QuoteSymbol quoteSymbol) {
        return MercadoBitcoinSymbolEnum.valueOf(quoteSymbol.name());
    }

}
