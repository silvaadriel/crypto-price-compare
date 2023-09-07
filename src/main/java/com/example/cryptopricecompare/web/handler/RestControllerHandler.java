package com.example.cryptopricecompare.web.handler;

import com.example.cryptopricecompare.exception.NotFoundException;
import com.example.cryptopricecompare.model.dto.CryptoPriceCompareErrorDTO;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
@Slf4j
public class RestControllerHandler {

    private final static String BUSINESS_RULE_CODE = "001";
    private final static String INVALID_ATTRIBUTE_CODE = "002";
    private final static String INVALID_ATTRIBUTE_MESSAGE = "Invalid attribute format or required.";
    private final static String INTERNAL_SERVER_ERROR_CODE = "003";
    private final static String INTERNAL_SERVER_ERROR_MESSAGE = "Unexpected internal server error.";

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CryptoPriceCompareErrorDTO handleNotFound(final HttpServletRequest req, final NotFoundException ex) {
        logErrorMessage(req, ex);
        return new CryptoPriceCompareErrorDTO(BUSINESS_RULE_CODE, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CryptoPriceCompareErrorDTO handleBadRequest(final HttpServletRequest req, final IllegalArgumentException ex) {
        logErrorMessage(req, ex);
        return new CryptoPriceCompareErrorDTO(INVALID_ATTRIBUTE_CODE, INVALID_ATTRIBUTE_MESSAGE, ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CryptoPriceCompareErrorDTO handleBadRequest(final HttpServletRequest req, final MissingServletRequestParameterException ex) {
        logErrorMessage(req, ex);
        return new CryptoPriceCompareErrorDTO(INVALID_ATTRIBUTE_CODE, INVALID_ATTRIBUTE_MESSAGE, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CryptoPriceCompareErrorDTO handleBadRequest(final HttpServletRequest req, final ConstraintViolationException ex) {
        logErrorMessage(req, ex);
        return new CryptoPriceCompareErrorDTO(INVALID_ATTRIBUTE_CODE, INVALID_ATTRIBUTE_MESSAGE, ex.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CryptoPriceCompareErrorDTO handleBadRequest(final HttpServletRequest req, final RestClientException ex) {
        logErrorMessage(req, ex);
        return new CryptoPriceCompareErrorDTO(INTERNAL_SERVER_ERROR_CODE, INTERNAL_SERVER_ERROR_MESSAGE, ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CryptoPriceCompareErrorDTO handleFeignException(final HttpServletRequest req, final FeignException ex) {
        logErrorMessage(req, ex);
        return new CryptoPriceCompareErrorDTO(INTERNAL_SERVER_ERROR_CODE, INTERNAL_SERVER_ERROR_MESSAGE, ex.contentUTF8());
    }

    private void logErrorMessage(final HttpServletRequest req, final Throwable ex) {
        log.error(req.getRequestURL().toString(), ex);
    }

}
