package com.example.cryptopricecompare.web.v1;

import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.example.cryptopricecompare.utils.Constants;
import com.example.cryptopricecompare.utils.MockUtils;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.cryptopricecompare.utils.MockUtils.mockGetApi;
import static com.example.cryptopricecompare.utils.ResourceUtils.getContentFile;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PriceApiDelegateImplJunit4Test {

    private static final String GET_PRICE_COMPARE_PATH = "/api/v1/price/compare?baseSymbol=%s&quoteSymbol=%s";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("classpath:json/binance-ticker-price-response-ok.json")
    private Resource binanceTickerPriceResponseOk;

    @Value("classpath:json/mercado-bitcoin-ticker-price-response-ok.json")
    private Resource mercadoBitcoinTickerPriceResponseOk;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Rule
    public WireMockRule binanceWireMock = new WireMockRule(Constants.BINANCE_HTTP_PORT);

    @Rule
    public WireMockRule mercadoBitcoinWireMock = new WireMockRule(Constants.MERCADO_BITCOIN_HTTP_PORT);

    @Test
    public void testGetPriceCompare_SymbolsSupportedForAllExchanges() throws Exception {
        mockGetApi(binanceWireMock, new MockUtils.MockApiParams(
                Constants.BINANCE_TICKER_PRICE_PATH.formatted(Constants.BINANCE_BTC_BRL), HttpStatus.OK,
                getContentFile(binanceTickerPriceResponseOk)));

        mockGetApi(mercadoBitcoinWireMock, new MockUtils.MockApiParams(
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
}
