package com.example.cryptopricecompare.web.v1;

import com.example.cryptopricecompare.api.PriceApiDelegate;
import com.example.cryptopricecompare.model.BaseSymbol;
import com.example.cryptopricecompare.model.PriceComparisonResponse;
import com.example.cryptopricecompare.model.QuoteSymbol;
import com.example.cryptopricecompare.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceApiDelegateImpl implements PriceApiDelegate {

    private final PriceService priceService;

    @Override
    public ResponseEntity<PriceComparisonResponse> getPriceCompare(BaseSymbol baseSymbol, QuoteSymbol quoteSymbol) {
        return ResponseEntity.status(HttpStatus.OK).body(priceService.getPriceCompare(baseSymbol, quoteSymbol));
    }
}
