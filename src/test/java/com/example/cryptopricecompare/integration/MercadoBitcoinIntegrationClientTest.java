package com.example.cryptopricecompare.integration;

import com.example.cryptopricecompare.model.dto.MercadoBitcoinTickerPriceDTO;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8082)
@SpringBootTest
class MercadoBitcoinIntegrationClientTest {

    @Autowired
    private MercadoBitcoinIntegrationClient mercadoBitcoinIntegrationClient;

    @Test
    public void testGetPriceBySymbols_200() {
        stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"pair\":\"BTC-BRL\",\"high\":\"130000.00000000\",\"low\":\"127000.06005000\",\"vol\":\"19.17774329\",\"last\":\"128559.72913616\",\"buy\":\"128550\",\"sell\":\"128595.3127362\",\"open\":\"128209.44308563\",\"date\":1694086627}]")));

        ResponseEntity<List<MercadoBitcoinTickerPriceDTO>> response = mercadoBitcoinIntegrationClient.getPriceBySymbols("BTC-BRL");

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("BTC-BRL", response.getBody().get(0).getPair());
        assertEquals("130000.00000000", response.getBody().get(0).getHigh());
        assertEquals("127000.06005000", response.getBody().get(0).getLow());
        assertEquals("19.17774329", response.getBody().get(0).getVol());
        assertEquals("128559.72913616", response.getBody().get(0).getLast());
        assertEquals("128550", response.getBody().get(0).getBuy());
        assertEquals("128595.3127362", response.getBody().get(0).getSell());
        assertEquals("128209.44308563", response.getBody().get(0).getOpen());
        assertEquals("1694086627", response.getBody().get(0).getDate());
    }

    @Test
    public void testGetPriceBySymbols_400() {
        stubFor(get(String.format("/api/v4/tickers?symbols=%s", "INVALID"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":\"PUBLIC_DATA|LIST_TICKERS|SYMBOLS_IS_REQUIRED\",\"message\":\"The param {symbols} must not be empty\"}")));

        FeignException ex = assertThrows(FeignException.BadRequest.class, () -> {
            mercadoBitcoinIntegrationClient.getPriceBySymbols("INVALID");
        });
        assertEquals("{\"code\":\"PUBLIC_DATA|LIST_TICKERS|SYMBOLS_IS_REQUIRED\",\"message\":\"The param {symbols} must not be empty\"}", ex.contentUTF8());
    }

    @Test
    public void testGetPriceBySymbols_500() {
        stubFor(get(String.format("/api/v4/tickers?symbols=%s", "INVALID"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("Internal server error")));

        FeignException ex = assertThrows(FeignException.InternalServerError.class, () -> {
            mercadoBitcoinIntegrationClient.getPriceBySymbols("INVALID");
        });
        assertEquals("Internal server error", ex.contentUTF8());
    }
}