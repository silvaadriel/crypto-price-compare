package com.example.cryptopricecompare.utils;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

public class MockUtils {

    @Data
    @Builder
    @AllArgsConstructor
    public static class MockApiParams {
        private String url;
        private HttpStatus httpStatus;
        private String responseBody;
    }

    public static void mockGetApi(MockApiParams mockApiParams) {
        stubFor(buildGetMapping(mockApiParams));
    }

    public static void mockGetApi(WireMockExtension wireMockExtension, MockApiParams mockApiParams) {
        wireMockExtension.stubFor(buildGetMapping(mockApiParams));
    }

    public static void mockGetApi(WireMockRule wireMockRule, MockApiParams mockApiParams) {
        wireMockRule.stubFor(buildGetMapping(mockApiParams));
    }

    private static MappingBuilder buildGetMapping(MockApiParams mockApiParams) {
        return get(mockApiParams.url)
                .willReturn(aResponse()
                        .withStatus(mockApiParams.httpStatus.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiParams.responseBody));
    }

}
