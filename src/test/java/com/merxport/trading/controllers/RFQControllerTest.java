package com.merxport.trading.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.Quote;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.entities.Unit;
import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.enumerations.RFQPriority;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.repositories.UnitRepository;
import com.merxport.trading.response.PageableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RFQControllerTest extends AbstractIntegrationTest
{
    
    @Autowired
    private CommodityRepository commodityRepository;
    
    @Autowired
    private UnitRepository unitRepository;
    
    private final ParameterizedTypeReference<PageableResponse> typeReference = new ParameterizedTypeReference<PageableResponse>()
    {
    };
    
    private final ParameterizedTypeReference<List<RFQ>> paramTypeReference = new ParameterizedTypeReference<List<RFQ>>()
    {
    };
    @Qualifier("getObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    private TypeReference<List<RFQ>> typeReferenceList = new TypeReference<>()
    {
    };
    
    @Test
    void saveRFQ()
    {
        RFQ rfq = getRFQ();
        ResponseEntity<RFQ> result = restTemplate.postForEntity("/api/rfq?token=" + AuthenticationController.TOKEN, rfq, RFQ.class);
        RFQ saved = result.getBody();
        assertNotNull(saved);
        assertEquals(rfq.getLocation(), saved.getLocation());
        assertNotNull(saved.getId());
    }
    
    @Test
    void deleteRFQ()
    {
        String id = "6132233e12e8966bf50d04eb";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        }};
        ResponseEntity<RFQ> deleted = restTemplate.getForEntity("/api/rfq/{id}/delete?token=" + AuthenticationController.TOKEN, RFQ.class, uriVars);
        RFQ rfq = deleted.getBody();
        assertNotNull(deleted);
        assertNotNull(rfq);
        assertFalse(rfq.isActive());
    }
    
    @Test
    void findByCommodityName()
    {
        String name = "Ergonomic";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("name", name);
        }};
        ResponseEntity<PageableResponse> result = restTemplate.exchange("/api/rfqByCommodityName/{name}?page=0&token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, typeReference, uriVars);
    
        PageableResponse pageableResponse = result.getBody();
        assertNotNull(pageableResponse);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertNotNull(rfqs);
        assertEquals(3, rfqs.size());
        assertEquals("Ergonomic Cotton Lamp", rfqs.get(0).getCommodity().getName());
    }
    
    @Test
    void findByCountry()
    {
        String country = "Nigeria";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("country", country);
        }};
        ResponseEntity<PageableResponse> result = restTemplate.exchange("/api/rfqByCountry/{country}?page=0&token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, typeReference, uriVars);
        PageableResponse pageableResponse = result.getBody();
        assertNotNull(pageableResponse);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertNotNull(rfqs);
        assertEquals(3, rfqs.size());
        assertEquals("Nigeria", rfqs.get(0).getCountry());
    }
    
    @Test
    void findByTerm()
    {
        CommercialTerms term = CommercialTerms.COST_INSURANCE_AND_FREIGHT;
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("term", term.name());
        }};
        ResponseEntity<PageableResponse> result = restTemplate.exchange("/api/rfqByTerm/{term}?page=0&token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, typeReference, uriVars);
        PageableResponse pageableResponse = result.getBody();
        assertNotNull(pageableResponse);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertNotNull(rfqs);
        assertEquals(3, rfqs.size());
        assertEquals("COST_INSURANCE_AND_FREIGHT", rfqs.get(0).getTerm().name());
    }
    
    @Test
    void findByTitle()
    {
        String title = "Granit";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("title", title);
        }};
        ResponseEntity<PageableResponse> result = restTemplate.exchange("/api/rfqByTitle/{title}?page=0&token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, typeReference, uriVars);
        PageableResponse pageableResponse = result.getBody();
        assertNotNull(pageableResponse);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertNotNull(rfqs);
        assertEquals(1, rfqs.size());
        assertEquals("Gorgeous Granite Gloves", rfqs.get(0).getTitle());
    }
    
    public RFQ getRFQ()
    {
        String imageID = "6126a4817f80646d7836a04f";
        String unitID = "61325d5dfce8ee74deff5415";
        Unit unit = unitRepository.findById(unitID).orElseThrow(EntityNotFoundException::new);
        Currency naira = Currency.getInstance("NGN");
        return new RFQ(faker.commerce().productName(),
                       commodityRepository.findById("6130ff9f4ed20e41b43a503c").orElse(new Commodity()),
                       new BigDecimal(20000), unit.getSingularName(),
                       faker.number().numberBetween(1, 100),
                       null, "QCDoc", RFQPriority.MEDIUM,
                       faker.number().numberBetween(10, 20),
                       faker.number().numberBetween(99, 100), LocalDateTime.now(),
                       naira, imageID, "Nigeria", CommercialTerms.COST_INSURANCE_AND_FREIGHT, "Lagos", "Good");
    }
}