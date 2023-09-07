package com.example.cryptopricecompare.service;

import com.example.cryptopricecompare.exception.NotFoundException;
import com.example.cryptopricecompare.integration.BinanceIntegrationClient;
import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.example.cryptopricecompare.model.dto.BinanceTickerPriceDTO;
import com.example.cryptopricecompare.model.enums.BinanceSymbolEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class BinanceService implements ExchangeService {

    private static final String SYMBOL_FORMAT = "%s%s";
    private static final Set<BinanceSymbolEnum> SUPPORTED_BASE_SYMBOLS = Set
            .of(BinanceSymbolEnum.BTC, BinanceSymbolEnum.ETH, BinanceSymbolEnum.LTC);
    private static final Set<BinanceSymbolEnum> SUPPORTED_QUOTE_SYMBOLS = Set
            .of(BinanceSymbolEnum.USD, BinanceSymbolEnum.EUR, BinanceSymbolEnum.BRL);

    private final BinanceIntegrationClient binanceIntegrationClient;

    @Override
    public String getExchangeName() {
        return "Binance";
    }

    @Override
    public boolean supportsSymbols(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol) {
        return SUPPORTED_BASE_SYMBOLS.contains(BinanceSymbolEnum.binanceSymbolOf(baseSymbol)) &&
                SUPPORTED_QUOTE_SYMBOLS.contains(BinanceSymbolEnum.binanceSymbolOf(quoteSymbol));
    }

    @Override
    public String getPrice(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol) {
        ResponseEntity<BinanceTickerPriceDTO> tickerPriceResponse = binanceIntegrationClient
                .getPriceBySymbol(formatSymbol(baseSymbol, quoteSymbol));

        if (isNull(tickerPriceResponse.getBody())) {
            throw new NotFoundException(String.format("%s price not found on %s", baseSymbol, getExchangeName()));
        }

        return tickerPriceResponse.getBody().getPrice();
    }

    private String formatSymbol(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol) {
        return String
                .format(SYMBOL_FORMAT, BinanceSymbolEnum.valueOf(baseSymbol), BinanceSymbolEnum.valueOf(quoteSymbol));
    }

}
