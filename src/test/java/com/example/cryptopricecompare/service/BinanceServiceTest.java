package com.example.cryptopricecompare.service;

import com.example.cryptopricecompare.exception.EmptyResponseBodyException;
import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8081)
@SpringBootTest
class BinanceServiceTest {

    @Autowired
    private BinanceService binanceService;

    @Test
    public void testGetExchangeName() {
        assertEquals("Binance", binanceService.getExchangeName());
    }

    @Test
    public void testSupportsSymbols_SupportedBaseSymbolAndQuoteSymbol() {
        assertTrue(binanceService.supportsSymbols(BaseSymbol.BTC, QuoteSymbol.BRL));
    }

    @Test
    public void testSupportsSymbols_SupportedBaseSymbolAndUnsupportedQuoteSymbol() {
        assertFalse(binanceService.supportsSymbols(BaseSymbol.BTC, QuoteSymbol.UNSUPPORTED));
    }

    @Test
    public void testSupportsSymbols_UnsupportedBaseSymbolAndSupportedQuoteSymbol() {
        assertFalse(binanceService.supportsSymbols(BaseSymbol.UNSUPPORTED, QuoteSymbol.BRL));
    }

    @Test
    public void testGetPrice() {
        stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"symbol\":\"BTCBRL\",\"price\":\"128710.00000000\"}")));

        String price = binanceService.getPrice(BaseSymbol.BTC, QuoteSymbol.BRL);

        assertEquals("128710.00000000", price);
    }

    @Test
    public void testGetPrice_EmptyResponseBody() {
        stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        EmptyResponseBodyException ex = assertThrows(EmptyResponseBodyException.class, () -> {
            binanceService.getPrice(BaseSymbol.BTC, QuoteSymbol.BRL);
        });
        assertEquals("Empty response body [Binance price service returned an empty response]", ex.getMessage());
    }
}