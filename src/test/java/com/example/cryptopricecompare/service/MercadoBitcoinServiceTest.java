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

@WireMockTest(httpPort = Constants.MERCADO_BITCOIN_HTTP_PORT)
@SpringBootTest
class MercadoBitcoinServiceTest {

    @Autowired
    private MercadoBitcoinService mercadoBitcoinService;

    @Value("classpath:json/mercado-bitcoin-ticker-price-response-ok.json")
    private Resource mercadoBitcoinTickerPriceResponseOk;

    @Test
    public void testGetExchangeName() {
        assertEquals("Mercado Bitcoin", mercadoBitcoinService.getExchangeName());
    }

    @Test
    public void testSupportsSymbols_SupportedBaseSymbolAndQuoteSymbol() {
        assertTrue(mercadoBitcoinService.supportsSymbols(BaseSymbol.BTC, QuoteSymbol.BRL));
    }

    @Test
    public void testSupportsSymbols_SupportedBaseSymbolAndUnsupportedQuoteSymbol() {
        assertFalse(mercadoBitcoinService.supportsSymbols(BaseSymbol.BTC, QuoteSymbol.UNSUPPORTED));
    }

    @Test
    public void testSupportsSymbols_UnsupportedBaseSymbolAndSupportedQuoteSymbol() {
        assertFalse(mercadoBitcoinService.supportsSymbols(BaseSymbol.UNSUPPORTED, QuoteSymbol.BRL));
    }

    @Test
    public void testGetPrice() {
        mockGetApi(new MockApiParams(Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL),
                HttpStatus.OK, getContentFile(mercadoBitcoinTickerPriceResponseOk)));

        String price = mercadoBitcoinService.getPrice(BaseSymbol.BTC, QuoteSymbol.BRL);

        assertEquals(Constants.MERCADO_BITCOIN_BTC_PRICE, price);
    }

    @Test
    public void testGetPrice_EmptyResponseBody() {
        mockGetApi(new MockApiParams(Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL),
                HttpStatus.OK, ""));

        EmptyResponseBodyException ex = assertThrows(EmptyResponseBodyException.class, () -> {
            mercadoBitcoinService.getPrice(BaseSymbol.BTC, QuoteSymbol.BRL);
        });
        assertEquals("Empty response body [Mercado Bitcoin price service returned an empty response]",
                ex.getMessage());
    }
}