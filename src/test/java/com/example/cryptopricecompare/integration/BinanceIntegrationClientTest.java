package com.example.cryptopricecompare.integration;

import com.example.cryptopricecompare.model.dto.BinanceTickerPriceDTO;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8081)
@SpringBootTest
class BinanceIntegrationClientTest {

    @Autowired
    private BinanceIntegrationClient binanceIntegrationClient;

    @Test
    public void testGetPriceBySymbol_200() {
        stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"symbol\":\"BTCBRL\",\"price\":\"128710.00000000\"}")));

        ResponseEntity<BinanceTickerPriceDTO> response = binanceIntegrationClient.getPriceBySymbol("BTCBRL");

        assertNotNull(response.getBody());
        assertEquals("BTCBRL", response.getBody().getSymbol());
        assertEquals("128710.00000000", response.getBody().getPrice());
    }

    @Test
    public void testGetPriceBySymbol_400() {
        stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "INVALID"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":-1121,\"msg\":\"Invalid symbol.\"}")));

        FeignException ex = assertThrows(FeignException.BadRequest.class, () -> {
            binanceIntegrationClient.getPriceBySymbol("INVALID");
        });
        assertEquals("{\"code\":-1121,\"msg\":\"Invalid symbol.\"}", ex.contentUTF8());
    }

    @Test
    public void testGetPriceBySymbol_500() {
        stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "INVALID"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("Internal server error")));

        FeignException ex = assertThrows(FeignException.InternalServerError.class, () -> {
            binanceIntegrationClient.getPriceBySymbol("INVALID");
        });
        assertEquals("Internal server error", ex.contentUTF8());
    }
}