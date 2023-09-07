package com.example.cryptopricecompare.service;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.ExchangePrice;
import com.example.cryptopricecompare.model.PriceComparisonResponse;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.example.cryptopricecompare.utils.Constants;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.example.cryptopricecompare.utils.MockUtils.MockApiParams;
import static com.example.cryptopricecompare.utils.MockUtils.mockGetApi;
import static com.example.cryptopricecompare.utils.ResourceUtils.getContentFile;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PriceServiceTest {

    @RegisterExtension
    public static WireMockExtension binanceWireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(Constants.BINANCE_HTTP_PORT))
            .build();

    @RegisterExtension
    public static WireMockExtension mercadoBitcoinWireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(Constants.MERCADO_BITCOIN_HTTP_PORT))
            .build();

    @Autowired
    private PriceService priceService;

    @Value("classpath:json/binance-ticker-price-response-ok.json")
    private Resource binanceTickerPriceResponseOk;

    @Value("classpath:json/binance-ticker-price-usd-response-ok.json")
    private Resource binanceTickerPriceUsdResponseOk;

    @Value("classpath:json/mercado-bitcoin-ticker-price-response-ok.json")
    private Resource mercadoBitcoinTickerPriceResponseOk;

    @Test
    public void testGetPriceCompare_SymbolsSupportedForAllExchanges() {
        mockGetApi(binanceWireMock, new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL),
                HttpStatus.OK, getContentFile(binanceTickerPriceResponseOk)));

        mockGetApi(mercadoBitcoinWireMock, new MockApiParams(
                Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL), HttpStatus.OK,
                getContentFile(mercadoBitcoinTickerPriceResponseOk)));

        ExchangePrice binance = new ExchangePrice()
                .exchange("Binance")
                .price(Constants.BINANCE_BTC_PRICE);
        ExchangePrice mercadoBitcoin = new ExchangePrice()
                .exchange("Mercado Bitcoin")
                .price(Constants.MERCADO_BITCOIN_BTC_PRICE);

        PriceComparisonResponse response = priceService.getPriceCompare(BaseSymbol.BTC, QuoteSymbol.BRL);

        assertEquals(2, response.getData().size());
        assertEquals(List.of(binance, mercadoBitcoin), response.getData());
    }

    @Test
    public void testGetPriceCompare_NotReturnUnsupportedSymbolForMercadoBitcoin() {
        mockGetApi(binanceWireMock, new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_USD),
                HttpStatus.OK, getContentFile(binanceTickerPriceUsdResponseOk)));

        PriceComparisonResponse response = priceService.getPriceCompare(BaseSymbol.BTC, QuoteSymbol.USD);

        ExchangePrice binance = new ExchangePrice()
                .exchange("Binance")
                .price(Constants.BINANCE_BTC_USD_PRICE);

        assertEquals(1, response.getData().size());
        assertEquals(List.of(binance), response.getData());
    }
}