package com.example.cryptopricecompare.integration;

import com.example.cryptopricecompare.model.dto.BinanceTickerPriceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "binance", url = "${integration.binance.api.v3.base-url}")
public interface BinanceIntegrationClient {
    @RequestMapping(method = RequestMethod.GET, value = "${integration.binance.api.v3.symbol-price-ticker}")
    ResponseEntity<BinanceTickerPriceDTO> getPriceBySymbol(@RequestParam String symbol);
}
