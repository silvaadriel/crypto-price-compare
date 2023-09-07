package com.example.cryptopricecompare.web.v1;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.example.cryptopricecompare.utils.Constants;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.cryptopricecompare.utils.MockUtils.MockApiParams;
import static com.example.cryptopricecompare.utils.MockUtils.mockGetApi;
import static com.example.cryptopricecompare.utils.ResourceUtils.getContentFile;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
class PriceApiDelegateImplTest {

    private static final String GET_PRICE_COMPARE_PATH = "/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s";

    @RegisterExtension
    public static WireMockExtension binanceWireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(Constants.BINANCE_HTTP_PORT))
            .build();

    @RegisterExtension
    public static WireMockExtension mercadoBitcoinWireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(Constants.MERCADO_BITCOIN_HTTP_PORT))
            .build();

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("classpath:json/binance-ticker-price-response-ok.json")
    private Resource binanceTickerPriceResponseOk;

    @Value("classpath:json/binance-ticker-price-usd-response-ok.json")
    private Resource binanceTickerPriceUsdResponseOk;

    @Value("classpath:json/binance-ticker-price-response-bad-request.json")
    private Resource binanceTickerPriceResponseBadRequest;

    @Value("classpath:json/mercado-bitcoin-ticker-price-response-ok.json")
    private Resource mercadoBitcoinTickerPriceResponseOk;

    @Value("classpath:json/mercado-bitcoin-ticker-price-response-bad-request.json")
    private Resource mercadoBitcoinTickerPriceResponseBadRequest;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void testGetPriceCompare_SymbolsSupportedForAllExchanges() throws Exception {
        mockGetApi(binanceWireMock, new MockApiParams(
                Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL), HttpStatus.OK,
                getContentFile(binanceTickerPriceResponseOk)));

        mockGetApi(mercadoBitcoinWireMock, new MockApiParams(
                Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL), HttpStatus.OK,
                getContentFile(mercadoBitcoinTickerPriceResponseOk)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(GET_PRICE_COMPARE_PATH.formatted(BaseSymbol.BTC, QuoteSymbol.BRL)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "data": [
                                    {
                                      "exchange": "Binance",
                                      "price": "%s"
                                    },
                                    {
                                      "exchange": "Mercado Bitcoin",
                                      "price": "%s"
                                    }
                                  ]
                                }
                                """.formatted(Constants.BINANCE_BTC_PRICE, Constants.MERCADO_BITCOIN_BTC_PRICE)));
    }

    @Test
    public void testGetPriceCompare_NotReturnUnsupportedSymbolForMercadoBitcoin() throws Exception {
        mockGetApi(binanceWireMock, new MockApiParams(
                Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_USD), HttpStatus.OK,
                getContentFile(binanceTickerPriceUsdResponseOk)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(GET_PRICE_COMPARE_PATH.formatted(BaseSymbol.BTC, QuoteSymbol.USD)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                  "data": [
                                    {
                                      "exchange": "Binance",
                                      "price": "%s"
                                    }
                                  ]
                                }
                                """.formatted(Constants.BINANCE_BTC_USD_PRICE)));
    }

    @Test
    public void testGetPriceCompare_BinanceEmptyResponseBody() throws Exception {
        mockGetApi(binanceWireMock, new MockApiParams(
                Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL), HttpStatus.OK, ""));

        mockGetApi(mercadoBitcoinWireMock, new MockApiParams(
                Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL), HttpStatus.OK,
                getContentFile(mercadoBitcoinTickerPriceResponseOk)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(GET_PRICE_COMPARE_PATH.formatted(BaseSymbol.BTC, QuoteSymbol.BRL)))
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
        mockGetApi(binanceWireMock, new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL),
                HttpStatus.OK, getContentFile(binanceTickerPriceResponseOk)));

        mockGetApi(mercadoBitcoinWireMock, new MockApiParams(
                Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL), HttpStatus.OK, ""));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(GET_PRICE_COMPARE_PATH.formatted(BaseSymbol.BTC, QuoteSymbol.BRL)))
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
        mockGetApi(binanceWireMock, new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL),
                HttpStatus.BAD_REQUEST, getContentFile(binanceTickerPriceResponseBadRequest)));

        mockGetApi(mercadoBitcoinWireMock, new MockApiParams(
                Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL), HttpStatus.OK,
                getContentFile(mercadoBitcoinTickerPriceResponseOk)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(GET_PRICE_COMPARE_PATH.formatted(BaseSymbol.BTC, QuoteSymbol.BRL)))
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
        mockGetApi(binanceWireMock, new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL),
                HttpStatus.OK, getContentFile(binanceTickerPriceResponseOk)));

        mockGetApi(mercadoBitcoinWireMock, new MockApiParams(
                Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL), HttpStatus.BAD_REQUEST,
                getContentFile(mercadoBitcoinTickerPriceResponseBadRequest)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(GET_PRICE_COMPARE_PATH.formatted(BaseSymbol.BTC, QuoteSymbol.BRL)))
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
        mockGetApi(binanceWireMock, new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL),
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));

        mockGetApi(mercadoBitcoinWireMock, new MockApiParams(
                Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL), HttpStatus.OK,
                getContentFile(mercadoBitcoinTickerPriceResponseOk)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(GET_PRICE_COMPARE_PATH.formatted(BaseSymbol.BTC, QuoteSymbol.BRL)))
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
        mockGetApi(binanceWireMock, new MockApiParams(Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL),
                HttpStatus.OK, getContentFile(binanceTickerPriceResponseOk)));

        mockGetApi(mercadoBitcoinWireMock, new MockApiParams(
                Constants.MERCADO_BITCOIN_TICKER_PRICE_PATH.formatted(Constants.MERCADO_BITCOIN_BTC_BRL),
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(GET_PRICE_COMPARE_PATH.formatted(BaseSymbol.BTC, QuoteSymbol.BRL)))
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
                        .get(GET_PRICE_COMPARE_PATH.formatted(Constants.INVALID_SYMBOL, QuoteSymbol.BRL)))
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
                        .get(GET_PRICE_COMPARE_PATH.formatted(BaseSymbol.BTC, Constants.INVALID_SYMBOL)))
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
                        .get(GET_PRICE_COMPARE_PATH.replace("baseSymbol=%s&", "").formatted(QuoteSymbol.BRL)))
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
                        .get(GET_PRICE_COMPARE_PATH.replace("&quoteSymbol=%s", "").formatted(BaseSymbol.BTC)))
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