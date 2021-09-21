package com.merxport.trading.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.entities.Unit;
import com.merxport.trading.repositories.UnitRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UnitControllerTest extends AbstractIntegrationTest
{
    @Autowired
    private UnitRepository unitRepository;
    
    private final ParameterizedTypeReference<List<Unit>> paramTypeReference = new ParameterizedTypeReference<List<Unit>>()
    {
    };
    @Qualifier("getObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    private TypeReference<List<Unit>> typeReferenceList = new TypeReference<>()
    {
    };
    
    @Test
    @Ignore
    void addUnit()
    {
        Unit unit = new Unit("Tonne", "Tonnes");
        ResponseEntity<Unit> response = restTemplate.postForEntity("/api/unit", unit, Unit.class);
        Unit savedUnit = response.getBody();
        assertNotNull(savedUnit);
        assertEquals(savedUnit.getSingularName(), unit.getSingularName());
    }
    
    @Test
    void getUnit()
    {
        String id = "61325d5dfce8ee74deff5415";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        }};
        ResponseEntity<Unit> response = restTemplate.getForEntity("/api/unit/{id}", Unit.class, uriVars);
        Unit retrievedUnit = response.getBody();
        assertNotNull(retrievedUnit);
        assertEquals("Bag", retrievedUnit.getSingularName());
        assertEquals(id, retrievedUnit.getId());
    }
    
    @Test
    void getUnits()
    {
        List<Unit> user = new ArrayList<>();
        ResponseEntity<List<Unit>> response = restTemplate.exchange("/api/units", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), paramTypeReference);
        List<Unit> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());
        assertTrue(body.get(0).getSingularName().equalsIgnoreCase("Bag"));
        assertTrue(body.get(0).getPluralName().equalsIgnoreCase("Bags"));
    }
}
