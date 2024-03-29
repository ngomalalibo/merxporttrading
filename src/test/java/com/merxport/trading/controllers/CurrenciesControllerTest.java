package com.merxport.trading.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.response.PageableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrenciesControllerTest extends AbstractIntegrationTest
{
    private final ParameterizedTypeReference<List<String>> paramTypeReference = new ParameterizedTypeReference<>()
    {
    };
    private TypeReference<List<Currency>> typeReferenceList = new TypeReference<>()
    {
    };
    @Test
    void getCurrency()
    {
        ResponseEntity<Currency> ngn = restTemplate.exchange("/api/currency/{code}", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(),  Currency.class, "NGN");
        assertNotNull(ngn.getBody());
        assertEquals("Nigerian Naira", ngn.getBody().getDisplayName());
    }
    
    @Test
    void getCurrencies()
    {
        ResponseEntity<List<String>> ngn = restTemplate.exchange("/api/currencies", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), paramTypeReference);
        assertNotNull(ngn.getBody());
        assertEquals(228, ngn.getBody().size());
    }
}
