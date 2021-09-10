package com.merxport.trading.controllers;

import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Unit;
import com.merxport.trading.repositories.UnitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UnitControllerTest extends AbstractIntegrationTest
{
    @Autowired
    private UnitRepository unitRepository;
    
    @Test
    void addUnit()
    {
        Unit unit = new Unit("Tonne", "Tonnes");
        ResponseEntity<Unit> response = restTemplate.postForEntity("/api/unit?token=" + AuthenticationController.TOKEN, unit, Unit.class);
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
        ResponseEntity<Unit> response = restTemplate.getForEntity("/api/unit/{id}?token=" + AuthenticationController.TOKEN, Unit.class, uriVars);
        Unit retrievedUnit = response.getBody();
        assertNotNull(retrievedUnit);
        assertEquals("Bag", retrievedUnit.getSingularName());
        assertEquals(id, retrievedUnit.getId());
    }
}
