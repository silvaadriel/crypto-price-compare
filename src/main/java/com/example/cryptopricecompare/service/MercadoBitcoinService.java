package com.example.cryptopricecompare.service;

import com.example.cryptopricecompare.exception.EmptyResponseBodyException;
import com.example.cryptopricecompare.integration.MercadoBitcoinIntegrationClient;
import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.example.cryptopricecompare.model.dto.MercadoBitcoinTickerPriceDTO;
import com.example.cryptopricecompare.model.enums.MercadoBitcoinSymbolEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class MercadoBitcoinService implements ExchangeService {

    private static final String SYMBOL_FORMAT = "%s-%s";
    private static final Set<MercadoBitcoinSymbolEnum> SUPPORTED_BASE_SYMBOLS = Set
            .of(MercadoBitcoinSymbolEnum.BTC, MercadoBitcoinSymbolEnum.ETH, MercadoBitcoinSymbolEnum.LTC);
    private static final Set<MercadoBitcoinSymbolEnum> SUPPORTED_QUOTE_SYMBOLS = Set.of(MercadoBitcoinSymbolEnum.BRL);

    private final MercadoBitcoinIntegrationClient mercadoBitcoinIntegrationClient;

    @Override
    public String getExchangeName() {
        return "Mercado Bitcoin";
    }

    @Override
    public boolean supportsSymbols(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol) {
        if (!MercadoBitcoinSymbolEnum.hasSymbol(baseSymbol) || !MercadoBitcoinSymbolEnum.hasSymbol(quoteSymbol)) {
            return false;
        }

        return SUPPORTED_BASE_SYMBOLS.contains(MercadoBitcoinSymbolEnum.mercadoBitcoinSymbolOf(baseSymbol)) &&
                SUPPORTED_QUOTE_SYMBOLS.contains(MercadoBitcoinSymbolEnum.mercadoBitcoinSymbolOf(quoteSymbol));
    }

    @Override
    public String getPrice(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol) {
        ResponseEntity<List<MercadoBitcoinTickerPriceDTO>> tickerPricesResponse = mercadoBitcoinIntegrationClient
                .getPriceBySymbols(formatSymbol(baseSymbol, quoteSymbol));

        if (isNull(tickerPricesResponse.getBody())) {
            throw new EmptyResponseBodyException(String.format("%s price service returned an empty response", getExchangeName()));
        }

        return tickerPricesResponse.getBody().get(0).getLast();
    }

    private String formatSymbol(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol) {
        return String.format(
                SYMBOL_FORMAT,
                MercadoBitcoinSymbolEnum.valueOf(baseSymbol),
                MercadoBitcoinSymbolEnum.valueOf(quoteSymbol)
        );
    }

}
