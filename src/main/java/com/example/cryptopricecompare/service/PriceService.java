package com.example.cryptopricecompare.service;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.ExchangePrice;
import com.example.cryptopricecompare.model.PriceComparisonResponse;
import com.example.cryptopricecompare.model.QuoteSymbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {

    private final List<ExchangeService> exchangeServices;

    public PriceComparisonResponse getPriceCompare(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol) {
        log.info("Searches for cryptocurrency prices on exchanges based on parameters [BaseSymbol: {}, QuoteSymbol: {}]", baseSymbol, quoteSymbol);

        List<ExchangePrice> exchangePrices = exchangeServices.stream()
                .filter(exchangeService -> exchangeService.supportsSymbols(baseSymbol, quoteSymbol))
                .map(exchangeService -> new ExchangePrice()
                        .exchange(exchangeService.getExchangeName())
                        .price(exchangeService.getPrice(baseSymbol, quoteSymbol)))
                .toList();

        return new PriceComparisonResponse().data(exchangePrices);
    }
}
