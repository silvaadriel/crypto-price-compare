package com.example.cryptopricecompare.integration;

import com.example.cryptopricecompare.model.dto.MercadoBitcoinTickerPriceDTO;
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

import java.util.List;

import static com.example.cryptopricecompare.utils.MockUtils.MockApiParams;
import static com.example.cryptopricecompare.utils.MockUtils.mockGetApi;
import static com.example.cryptopricecompare.utils.ResourceUtils.getContentFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest(httpPort = Constants.MERCADO_BITCOIN_HTTP_PORT)
@SpringBootTest
class MercadoBitcoinIntegrationClientTest {

    @Autowired
    private MercadoBitcoinIntegrationClient mercadoBitcoinIntegrationClient;

    @Value("classpath:json/mercado-bitcoin-ticker-price-response-ok.json")
    private Resource mercadoBitcoinTickerPriceResponseOk;

    @Value("classpath:json/mercado-bitcoin-ticker-price-response-bad-request.json")
    private Resource mercadoBitcoinTickerPriceResponseBadRequest;

    @Test
    public void testGetPriceBySymbols_200() {
        mockGetApi(new MockApiParams(Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL),
                HttpStatus.OK, getContentFile(mercadoBitcoinTickerPriceResponseOk)));

        ResponseEntity<List<MercadoBitcoinTickerPriceDTO>> response = mercadoBitcoinIntegrationClient
                .getPriceBySymbols(Constants.MERCADO_BITCOIN_BTC_BRL);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(Constants.MERCADO_BITCOIN_BTC_BRL, response.getBody().get(0).getPair());
        assertEquals(Constants.MERCADO_BITCOIN_BTC_PRICE, response.getBody().get(0).getLast());
    }

    @Test
    public void testGetPriceBySymbols_400() {
        String bodyContent = getContentFile(mercadoBitcoinTickerPriceResponseBadRequest);

        mockGetApi(new MockApiParams(Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.INVALID_SYMBOL),
                HttpStatus.BAD_REQUEST, bodyContent));

        FeignException ex = assertThrows(FeignException.BadRequest.class, () -> {
            mercadoBitcoinIntegrationClient.getPriceBySymbols(Constants.INVALID_SYMBOL);
        });
        assertEquals(bodyContent, ex.contentUTF8());
    }

    @Test
    public void testGetPriceBySymbols_500() {
        String bodyContent = "Internal server error";

        mockGetApi(new MockApiParams(Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.INVALID_SYMBOL),
                HttpStatus.INTERNAL_SERVER_ERROR, bodyContent));

        FeignException ex = assertThrows(FeignException.InternalServerError.class, () -> {
            mercadoBitcoinIntegrationClient.getPriceBySymbols(Constants.INVALID_SYMBOL);
        });
        assertEquals(bodyContent, ex.contentUTF8());
    }
}