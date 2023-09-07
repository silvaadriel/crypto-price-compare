package com.example.cryptopricecompare.service;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;

public interface ExchangeService {
    String getExchangeName();
    boolean supportsSymbols(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol);
    String getPrice(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol);
}
