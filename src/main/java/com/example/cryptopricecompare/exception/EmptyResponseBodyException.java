package com.example.cryptopricecompare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.NOT_FOUND)
public class EmptyResponseBodyException extends RuntimeException {

    public EmptyResponseBodyException(String message) {
        super(String.format("%s [%s]", "Empty response body", message));
    }

}
