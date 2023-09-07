package com.example.cryptopricecompare.web.v1;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
class PriceApiDelegateImplTest {

    @RegisterExtension
    public static WireMockExtension binanceWireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8081))
            .build();

    @RegisterExtension
    public static WireMockExtension mercadoBitcoinWireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8082))
            .build();

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void testGetPriceCompare_SymbolsSupportedForAllExchanges() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", BaseSymbol.BTC, QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "data": [
                                    {
                                      "exchange": "Binance",
                                      "price": "128710.00000000"
                                    },
                                    {
                                      "exchange": "Mercado Bitcoin",
                                      "price": "128559.72913616"
                                    }
                                  ]
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_NotReturnUnsupportedSymbolForMercadoBitcoin() throws Exception {
        binanceWireMock.stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCUSDT"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"symbol\":\"BTCUSDT\",\"price\":\"128710.00000000\"}")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", BaseSymbol.BTC, QuoteSymbol.USD)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "data": [
                                    {
                                      "exchange": "Binance",
                                      "price": "128710.00000000"
                                    }
                                  ]
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_BinanceEmptyResponseBody() throws Exception {
        binanceWireMock.stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        mercadoBitcoinWireMock.stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"pair\":\"BTC-BRL\",\"high\":\"130000.00000000\",\"low\":\"127000.06005000\",\"vol\":\"19.17774329\",\"last\":\"128559.72913616\",\"buy\":\"128550\",\"sell\":\"128595.3127362\",\"open\":\"128209.44308563\",\"date\":1694086627}]")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", BaseSymbol.BTC, QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "001",
                                  "description": "Empty response body [Binance price service returned an empty response]"
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_MercadoBitcoinEmptyResponseBody() throws Exception {
        binanceWireMock.stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"symbol\":\"BTCBRL\",\"price\":\"128710.00000000\"}")));

        mercadoBitcoinWireMock.stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", BaseSymbol.BTC, QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "001",
                                  "description": "Empty response body [Mercado Bitcoin price service returned an empty response]"
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_BinanceBadRequest() throws Exception {
        binanceWireMock.stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":-1121,\"msg\":\"Invalid symbol.\"}")));

        mercadoBitcoinWireMock.stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"pair\":\"BTC-BRL\",\"high\":\"130000.00000000\",\"low\":\"127000.06005000\",\"vol\":\"19.17774329\",\"last\":\"128559.72913616\",\"buy\":\"128550\",\"sell\":\"128595.3127362\",\"open\":\"128209.44308563\",\"date\":1694086627}]")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", BaseSymbol.BTC, QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "003",
                                  "description": "Unexpected internal server error. [{\\"code\\":-1121,\\"msg\\":\\"Invalid symbol.\\"}]"
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_MercadoBitcoinBadRequest() throws Exception {
        binanceWireMock.stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"symbol\":\"BTCBRL\",\"price\":\"128710.00000000\"}")));

        mercadoBitcoinWireMock.stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":\"PUBLIC_DATA|LIST_TICKERS|SYMBOLS_IS_REQUIRED\",\"message\":\"The param {symbols} must not be empty\"}")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", BaseSymbol.BTC, QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "003",
                                  "description": "Unexpected internal server error. [{\\"code\\":\\"PUBLIC_DATA|LIST_TICKERS|SYMBOLS_IS_REQUIRED\\",\\"message\\":\\"The param {symbols} must not be empty\\"}]"
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_BinanceInternalServerError() throws Exception {
        binanceWireMock.stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("Internal server error")));

        mercadoBitcoinWireMock.stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"pair\":\"BTC-BRL\",\"high\":\"130000.00000000\",\"low\":\"127000.06005000\",\"vol\":\"19.17774329\",\"last\":\"128559.72913616\",\"buy\":\"128550\",\"sell\":\"128595.3127362\",\"open\":\"128209.44308563\",\"date\":1694086627}]")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", BaseSymbol.BTC, QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "003",
                                  "description": "Unexpected internal server error. [Internal server error]"
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_MercadoBitcoinInternalServerError() throws Exception {
        binanceWireMock.stubFor(get(String.format("/api/v3/ticker/price?symbol=%s", "BTCBRL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"symbol\":\"BTCBRL\",\"price\":\"128710.00000000\"}")));

        mercadoBitcoinWireMock.stubFor(get(String.format("/api/v4/tickers?symbols=%s", "BTC-BRL"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("Internal server error")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", BaseSymbol.BTC, QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "003",
                                  "description": "Unexpected internal server error. [Internal server error]"
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_InvalidBaseSymbol() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", "INVALID", QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "002",
                                  "description": "Invalid attribute format or required. [No enum constant com.example.cryptopricecompare.model.BaseSymbol.INVALID]"
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_InvalidQuoteSymbol() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s", BaseSymbol.BTC, "INVALID")))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "002",
                                  "description": "Invalid attribute format or required. [No enum constant com.example.cryptopricecompare.model.QuoteSymbol.INVALID]"
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_MissingBaseSymbol() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?quoteSymbol=%s", QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "002",
                                  "description": "Invalid attribute format or required. [Required request parameter 'baseSymbol' for method parameter type BaseSymbol is not present]"
                                }
                                """));
    }

    @Test
    public void testGetPriceCompare_MissingQuoteSymbol() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/api/v1/price/compare?baseSymbol=%s", BaseSymbol.BTC)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "code": "002",
                                  "description": "Invalid attribute format or required. [Required request parameter 'quoteSymbol' for method parameter type QuoteSymbol is not present]"
                                }
                                """));
    }
}