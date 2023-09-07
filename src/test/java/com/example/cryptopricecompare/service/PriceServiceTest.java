package com.example.cryptopricecompare.service;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.ExchangePrice;
import com.example.cryptopricecompare.model.PriceComparisonResponse;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PriceServiceTest {

    @RegisterExtension
    public static WireMockExtension binanceWireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8081))
            .build();

    @RegisterExtension
    public static WireMockExtension mercadoBitcoinWireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8082))
            .build();

    @Autowired
    private PriceService priceService;

    @Test
    public void testGetPriceCompare_SymbolsSupportedForAllExchanges() {
        binanceWireMock.stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"symbol\":\"BTCBRL\",\"price\":\"128710.00000000\"}")));

        mercadoBitcoinWireMock.stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"pair\":\"BTC-BRL\",\"high\":\"130000.00000000\",\"low\":\"127000.06005000\",\"vol\":\"19.17774329\",\"last\":\"128559.72913616\",\"buy\":\"128550\",\"sell\":\"128595.3127362\",\"open\":\"128209.44308563\",\"date\":1694086627}]")));

        ExchangePrice binance = new ExchangePrice()
                .exchange("Binance")
                .price("128710.00000000");
        ExchangePrice mercadoBitcoin = new ExchangePrice()
                .exchange("Mercado Bitcoin")
                .price("128559.72913616");

        PriceComparisonResponse response = priceService.getPriceCompare(BaseSymbol.BTC, QuoteSymbol.BRL);

        assertEquals(2, response.getData().size());
        assertEquals(List.of(binance, mercadoBitcoin), response.getData());
    }

    @Test
    public void testGetPriceCompare_NotReturnUnsupportedSymbolForMercadoBitcoin() {
        binanceWireMock.stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCUSDT"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"symbol\":\"BTCUSDT\",\"price\":\"25696.75000000\"}")));

        PriceComparisonResponse response = priceService.getPriceCompare(BaseSymbol.BTC, QuoteSymbol.USD);

        ExchangePrice binance = new ExchangePrice()
                .exchange("Binance")
                .price("25696.75000000");

        assertEquals(1, response.getData().size());
        assertEquals(List.of(binance), response.getData());
    }
}