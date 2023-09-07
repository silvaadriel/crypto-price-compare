package com.example.cryptopricecompare.integration;

import com.example.cryptopricecompare.model.dto.MercadoBitcoinTickerPriceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "mercado-bitcoin", url = "${integration.mercado-bitcoin.api.v4.base-url}")
public interface MercadoBitcoinIntegrationClient {
    @RequestMapping(method = RequestMethod.GET, value = "${integration.mercado-bitcoin.api.v4.symbol-price-ticker}")
    ResponseEntity<List<MercadoBitcoinTickerPriceDTO>> getPriceBySymbols(@RequestParam String symbols);
}
