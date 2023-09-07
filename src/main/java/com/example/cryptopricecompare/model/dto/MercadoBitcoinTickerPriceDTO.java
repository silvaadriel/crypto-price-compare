package com.example.cryptopricecompare.model.dto;

import lombok.Data;

@Data
public class MercadoBitcoinTickerPriceDTO {
    private String pair;
    private String high;
    private String low;
    private String vol;
    private String last;
    private String buy;
    private String sell;
    private String open;
    private String date;
}
