package com.example.cryptopricecompare.model.dto;

import lombok.Data;

@Data
public class BinanceTickerPriceDTO {
    private String symbol;
    private String price;
}
