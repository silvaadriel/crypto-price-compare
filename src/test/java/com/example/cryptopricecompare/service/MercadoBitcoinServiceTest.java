package com.example.cryptopricecompare.service;

import com.example.cryptopricecompare.exception.EmptyResponseBodyException;
import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8082)
@SpringBootTest
class MercadoBitcoinServiceTest {

    @Autowired
    private MercadoBitcoinService mercadoBitcoinService;

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
        stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"pair\":\"BTC-BRL\",\"high\":\"130000.00000000\",\"low\":\"127000.06005000\",\"vol\":\"19.17774329\",\"last\":\"128559.72913616\",\"buy\":\"128550\",\"sell\":\"128595.3127362\",\"open\":\"128209.44308563\",\"date\":1694086627}]")));

        String price = mercadoBitcoinService.getPrice(BaseSymbol.BTC, QuoteSymbol.BRL);

        assertEquals("128559.72913616", price);
    }

    @Test
    public void testGetPrice_EmptyResponseBody() {
        stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        EmptyResponseBodyException ex = assertThrows(EmptyResponseBodyException.class, () -> {
            mercadoBitcoinService.getPrice(BaseSymbol.BTC, QuoteSymbol.BRL);
        });
        assertEquals("Empty response body [Mercado Bitcoin price service returned an empty response]", ex.getMessage());
    }
}