package com.example.cryptopricecompare.service;

import com.example.cryptopricecompare.exception.EmptyResponseBodyException;
import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.example.cryptopricecompare.utils.Constants;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import static com.example.cryptopricecompare.utils.MockUtils.MockApiParams;
import static com.example.cryptopricecompare.utils.MockUtils.mockGetApi;
import static com.example.cryptopricecompare.utils.ResourceUtils.getContentFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WireMockTest(httpPort = Constants.BINANCE_HTTP_PORT)
@SpringBootTest
class BinanceServiceTest {

    @Autowired
    private BinanceService binanceService;

    @Value("classpath:json/binance-ticker-price-response-ok.json")
    private Resource binanceTickerPriceResponseOk;

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
        mockGetApi(new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL),
                HttpStatus.OK, getContentFile(binanceTickerPriceResponseOk)));

        String price = binanceService.getPrice(BaseSymbol.BTC, QuoteSymbol.BRL);

        assertEquals(Constants.BINANCE_BTC_PRICE, price);
    }

    @Test
    public void testGetPrice_EmptyResponseBody() {
        mockGetApi(new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL),
                HttpStatus.OK, ""));

        EmptyResponseBodyException ex = assertThrows(EmptyResponseBodyException.class, () -> {
            binanceService.getPrice(BaseSymbol.BTC, QuoteSymbol.BRL);
        });
        assertEquals("Empty response body [Binance price service returned an empty response]", ex.getMessage());
    }
}