package com.merxport.trading.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.entities.Unit;
import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.enumerations.RFQPriority;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.repositories.UnitRepository;
import com.merxport.trading.response.PageableResponse;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
    @Ignore
    @Test
    void saveRFQ()
    {
        RFQ rfq = getRFQ();
        ResponseEntity<RFQ> result = restTemplate.postForEntity("/api/rfq", rfq, RFQ.class);
        RFQ saved = result.getBody();
        assertNotNull(saved);
        assertEquals(rfq.getLocation(), saved.getLocation());
        assertNotNull(saved.getId());
    }
    
    @Test
    void getRFQById()
    {
        String id = "61474af86ea3281010dbc7bf";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        }};
    
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+AuthenticationController.TOKEN);
        final HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        
        ResponseEntity<RFQ> found = restTemplate.exchange("/api/rfq/{id}", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), RFQ.class, uriVars);
        assertNotNull(found);
        RFQ rfq = found.getBody();
        assertNotNull(rfq);
        assertEquals("another request", rfq.getTitle());
        System.out.println(rfq.getSampleImage());
        assertNotNull(rfq.getSampleImage());
        assertNotEquals(rfq.getSampleImage(), "");
        
    }
    
    @Test
    @Ignore
    void deleteRFQ()
    {
        String id = "6132233e12e8966bf50d04eb";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        }};
        ResponseEntity<RFQ> deleted = restTemplate.getForEntity("/api/rfq/{id}/delete", RFQ.class, uriVars);
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
        ResponseEntity<PageableResponse> result = restTemplate.exchange("/api/rfqByCommodityName/{name}?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        
        PageableResponse pageableResponse = result.getBody();
        assertNotNull(pageableResponse);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertNotNull(rfqs);
        assertEquals(3, rfqs.size());
        assertEquals("6130ff9f4ed20e41b43a503c", rfqs.get(0).getCommodityID());
    }
    
    @Test
    void findByCountry()
    {
        String country = "Nigeria";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("country", country);
        }};
        ResponseEntity<PageableResponse> result = restTemplate.exchange("/api/rfqByCountry/{country}?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
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
        ResponseEntity<PageableResponse> result = restTemplate.exchange("/api/rfqByTerm/{term}?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
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
        ResponseEntity<PageableResponse> result = restTemplate.exchange("/api/rfqByTitle/{title}?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = result.getBody();
        assertNotNull(pageableResponse);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertNotNull(rfqs);
        assertEquals(1, rfqs.size());
        assertEquals("Gorgeous Granite Gloves", rfqs.get(0).getTitle());
    }
    
    @Test
    void findByBuyer()
    {
        String buyerID = "6126806273aade16270429c4";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("buyerID", buyerID);
        }};
        ResponseEntity<PageableResponse> result = restTemplate.exchange("/api/rfq/{buyerID}/buyer?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = result.getBody();
        assertNotNull(pageableResponse);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertNotNull(rfqs);
        assertEquals(1, rfqs.size());
        assertEquals("Enormous Silk Knife", rfqs.get(0).getTitle());
    }
    
    public RFQ getRFQ()
    {
        String imageID = "6126a4817f80646d7836a04f";
        String unitID = "61325d5dfce8ee74deff5415";
        Unit unit = unitRepository.findById(unitID).orElseThrow(EntityNotFoundException::new);
        Currency naira = Currency.getInstance("NGN");
        return new RFQ(faker.commerce().productName(),
                       "6130ff9f4ed20e41b43a503c",
                       new BigDecimal(20000), unit.getSingularName(),
                       faker.number().numberBetween(1, 100),
                       null, "QCDoc", RFQPriority.MEDIUM,
                       faker.number().numberBetween(10, 20),
                       faker.number().numberBetween(99, 100), LocalDateTime.now(),
                       naira, imageID, null, "Nigeria", CommercialTerms.COST_INSURANCE_AND_FREIGHT, "Lagos", "Good", "6126806273aade16270429c4", new BigDecimal(300000), new BigDecimal(350000));
    }
    
    public static void main(String[] args)
    {
        System.out.println(LocalDateTime.now());
    }
}
