package com.example.cryptopricecompare.model.enums;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BinanceSymbolEnum {

    BTC("BTC"),
    ETH("ETH"),
    LTC("LTC"),
    USD("USDT"),
    EUR("EUR"),
    BRL("BRL");

    private final String value;

    public static String valueOf(BaseSymbol baseSymbol) {
        return BinanceSymbolEnum.valueOf(baseSymbol.name()).getValue();
    }

    public static String valueOf(QuoteSymbol quoteSymbol) {
        return BinanceSymbolEnum.valueOf(quoteSymbol.name()).getValue();
    }

    public static BinanceSymbolEnum binanceSymbolOf(BaseSymbol baseSymbol) {
        return BinanceSymbolEnum.valueOf(baseSymbol.name());
    }

    public static BinanceSymbolEnum binanceSymbolOf(QuoteSymbol quoteSymbol) {
        return BinanceSymbolEnum.valueOf(quoteSymbol.name());
    }

}
