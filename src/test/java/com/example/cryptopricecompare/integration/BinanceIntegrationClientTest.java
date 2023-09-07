package com.example.cryptopricecompare.integration;

import com.example.cryptopricecompare.model.dto.BinanceTickerPriceDTO;
import com.example.cryptopricecompare.utils.Constants;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.example.cryptopricecompare.utils.MockUtils.MockApiParams;
import static com.example.cryptopricecompare.utils.MockUtils.mockGetApi;
import static com.example.cryptopricecompare.utils.ResourceUtils.getContentFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest(httpPort = Constants.BINANCE_HTTP_PORT)
@SpringBootTest
class BinanceIntegrationClientTest {

    @Autowired
    private BinanceIntegrationClient binanceIntegrationClient;

    @Value("classpath:json/binance-ticker-price-response-ok.json")
    private Resource binanceTickerPriceResponseOk;

    @Value("classpath:json/binance-ticker-price-response-bad-request.json")
    private Resource binanceTickerPriceResponseBadRequest;

    @Test
    public void testGetPriceBySymbol_200() {
        mockGetApi(new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL),
                HttpStatus.OK, getContentFile(binanceTickerPriceResponseOk)));

        ResponseEntity<BinanceTickerPriceDTO> response = binanceIntegrationClient
                .getPriceBySymbol(Constants.BINANCE_BTC_BRL);

        assertNotNull(response.getBody());
        assertEquals(Constants.BINANCE_BTC_BRL, response.getBody().getSymbol());
        assertEquals(Constants.BINANCE_BTC_PRICE, response.getBody().getPrice());
    }

    @Test
    public void testGetPriceBySymbol_400() {
        String bodyContent = getContentFile(binanceTickerPriceResponseBadRequest);

        mockGetApi(new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.INVALID_SYMBOL),
                HttpStatus.BAD_REQUEST, bodyContent));

        FeignException ex = assertThrows(FeignException.BadRequest.class, () -> {
            binanceIntegrationClient.getPriceBySymbol(Constants.INVALID_SYMBOL);
        });
        assertEquals(bodyContent, ex.contentUTF8());
    }

    @Test
    public void testGetPriceBySymbol_500() {
        String bodyContent = "Internal server error";

        mockGetApi(new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.INVALID_SYMBOL),
                HttpStatus.INTERNAL_SERVER_ERROR, bodyContent));

        FeignException ex = assertThrows(FeignException.InternalServerError.class, () -> {
            binanceIntegrationClient.getPriceBySymbol(Constants.INVALID_SYMBOL);
        });
        assertEquals(bodyContent, ex.contentUTF8());
    }
}