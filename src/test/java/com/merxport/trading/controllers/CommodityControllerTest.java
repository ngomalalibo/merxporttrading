package com.merxport.trading.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.CommodityRequest;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.response.PageableResponse;
import com.merxport.trading.services.UserService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class CommodityControllerTest extends AbstractIntegrationTest
{
    private FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-GB"), new RandomService());
    private final Faker faker = new Faker(Locale.getDefault());
    
    @Autowired
    private UserService userService;
    
    private Commodity commodity;
    
    private User user;
    
    private final ParameterizedTypeReference<PageableResponse> typeReference = new ParameterizedTypeReference<>()
    {
    };
    
    private final ParameterizedTypeReference<List<Commodity>> paramTypeReference = new ParameterizedTypeReference<>()
    {
    };
    @Qualifier("getObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    private TypeReference<List<Commodity>> typeReferenceList = new TypeReference<>()
    {
    };
    
    @BeforeEach
    public void setup() throws Exception
    {
        File file = new File("./src/main/resources/static/images/team4.jpg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("image.jpg", file.getName(), Files.probeContentType(file.toPath()), IOUtils.toByteArray(input));
        
        // String imageID = userService.upload(multipartFile);
        //Address address = new Address("street", "city", "state", "country");
        
        //User user = new User(faker.name().firstName(), faker.name().lastName(), faker.name().nameWithMiddle(), faker.internet().emailAddress(), faker.internet().password(), faker.phoneNumber().phoneNumber(), Collections.singletonList(address), false, UserScopes.DOMESTIC, imageID, List.of(UserRole.BUYER, UserRole.BUYER), null, null, null, null, null, null, false, null);
        //user = userService.save(user);
        
        String imageID = "6126a4817f80646d7836a04f";
        user = userService.findByID("6126a4897f80646d7836a051");
        commodity = new Commodity(faker.commerce().productName(), Collections.singletonList(faker.company().industry()), "Description2", new HashMap<>(), "QCDoc2", Collections.singletonList(imageID), new BigDecimal(100000), 1, "Bag", user.getId(), faker.country().name(), Scopes.INTERNATIONAL);
    }
    
    
    @Test
    void addCommodity() throws Exception
    {
        System.out.println("Token: " + user.getToken());
        ResponseEntity<Commodity> cr = restTemplate.postForEntity("/api/commodity?token=" + user.getToken(), commodity, Commodity.class);
        assertNotNull(cr);
        Commodity body = cr.getBody();
        assertNotNull(body);
        assertEquals(commodity.getName(), body.getName());
    }
    
    
    @Test
    void getCommodity()
    {
        String id = "6126aec44fde1f4ed06f55a4";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        }};
        Commodity body = restTemplate.getForEntity("/api/commodity/{id}", Commodity.class, uriVars).getBody();
        assertNotNull(body);
        assertEquals(body.getName(), "Sleek Bronze Wallet");
    }
    
    @Test
    void updateCommodity()
    {
        String id = "6126aec44fde1f4ed06f55a4";
        String name = "Sleek Bronze Wallet Updated";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        }};
        Commodity body = restTemplate.getForEntity("/api/commodity/{id}", Commodity.class, uriVars).getBody();
        assertNotNull(body);
        body.setName(name);
        restTemplate.put("/api/commodity", body, Commodity.class, uriVars);
        Commodity saved = restTemplate.getForEntity("/api/commodity/{id}", Commodity.class, uriVars).getBody();
        assertNotNull(saved);
        assertEquals(name, saved.getName());
    }
    
    @Test
    void getCommoditiesByName()
    {
        // String name = "W";
        String name = "co";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("name", name);
        }};
        ResponseEntity<PageableResponse> body = restTemplate.exchange("/api/commodities/{name}?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = body.getBody();
        assertNotNull(pageableResponse);
        List<Commodity> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertFalse(responseBody.stream().map(commodity -> commodity.getName().toLowerCase()).anyMatch(d -> !d.contains(name.toLowerCase())));
        assertEquals(3, responseBody.size());
    }
    
    @Test
    void getCommoditiesSearch()
    {
        String search = "Ergonomic";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("search", search);
        }};
        
        ResponseEntity<PageableResponse> body = restTemplate.exchange("/api/{search}/commoditySearch?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = body.getBody();
        assertNotNull(pageableResponse);
        List<Commodity> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        responseBody.stream().map(Commodity::getName).forEach(System.out::println);
        assertEquals(2, responseBody.size());
    }
    
    @Test
    void deleteCommodity()
    {
        String id = "61278177e31b270463cf7dce";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        }};
        ResponseEntity<Commodity> body = restTemplate.exchange("/api/commodity/{id}/delete", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), new ParameterizedTypeReference<Commodity>()
        {
        }, uriVars);
        assertNotNull(body);
        assertNotNull(body.getBody());
        assertFalse(body.getBody().isActive());
    }
    
    @Test
    void findCommodityByCategoryLike()
    {
        String category = "u";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("category", category);
        }};
        
        ResponseEntity<PageableResponse> body = restTemplate.exchange("/api/{category}/commodityByCategory?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = body.getBody();
        assertNotNull(pageableResponse);
        List<Commodity> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        Assertions.assertEquals(3, responseBody.size());
        Assertions.assertEquals("Public Safety", responseBody.get(0).getCategory().get(0));
    }
    
    @Test
    void findCommodityByCountry()
    {
        String country = "Iceland";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("country", country);
        }};
        
        ResponseEntity<PageableResponse> body = restTemplate.exchange("/api/{country}/commodityByCountry?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = body.getBody();
        assertNotNull(pageableResponse);
        List<Commodity> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        Assertions.assertEquals(1, responseBody.size());
        Assertions.assertEquals("Iceland", responseBody.get(0).getCountry());
    }
    
    @Test
    void findCommodityByAmountGreaterThan()
    {
        BigDecimal amount = new BigDecimal(30000);
        Map<String, BigDecimal> uriVars = new HashMap<>()
        {{
            put("amount", amount);
        }};
        
        ResponseEntity<PageableResponse> body = restTemplate.exchange("/api/{amount}/commodityGreaterThan?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = body.getBody();
        assertNotNull(pageableResponse);
        List<Commodity> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        Assertions.assertEquals(6, responseBody.size());
        Assertions.assertEquals(new BigDecimal(100000), responseBody.get(0).getRate());
    }
    
    @Test
    void findCommodityByAmountLessThan()
    {
        BigDecimal amount = new BigDecimal(40000);
        Map<String, BigDecimal> uriVars = new HashMap<>()
        {{
            put("amount", amount);
        }};
        
        ResponseEntity<PageableResponse> body = restTemplate.exchange("/api/{amount}/commodityLessThan?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = body.getBody();
        assertNotNull(pageableResponse);
        List<Commodity> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        Assertions.assertEquals(1, responseBody.size());
        Assertions.assertEquals(new BigDecimal(30000), responseBody.get(0).getRate());
    }
    
    @Test
    void findCommodityByScope()
    {
        Scopes scope = Scopes.INTERNATIONAL;
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("scope", scope.name());
        }};
        
        ResponseEntity<PageableResponse> body = restTemplate.exchange("/api/{scope}/commodityByScope?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = body.getBody();
        assertNotNull(pageableResponse);
        List<Commodity> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        Assertions.assertEquals(6, responseBody.size());
        Assertions.assertEquals("INTERNATIONAL", responseBody.get(0).getScope().name());
    }
    
    @Test
    void findCommodityBySeller()
    {
        String sellerId = "6126a4897f80646d7836a051";
        User user = new User();
        user.setId(sellerId);
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("sellerID", user.getId());
        }};
        
        ResponseEntity<PageableResponse> body = restTemplate.exchange("/api/{sellerID}/commodityBySeller?page=1&pageSize=6", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), typeReference, uriVars);
        PageableResponse pageableResponse = body.getBody();
        assertNotNull(pageableResponse);
        List<Commodity> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        Assertions.assertEquals(6, responseBody.size());
        assertTrue(responseBody.get(0).getDescription().contains("Descriptio"));
    }
    
    @Test
    void findCommoditySearch()
    {
        CommodityRequest cr = new CommodityRequest("Iceland", Scopes.INTERNATIONAL.name(), new BigDecimal(40000), "Services");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + AuthenticationController.TOKEN);
        HttpEntity<CommodityRequest> requestEntity = new HttpEntity<>(cr, headers);
        
        ResponseEntity<PageableResponse> body = restTemplate.exchange("https://merxporttrading.herokuapp.com/api/commodityMultiSearch?page=1&pageSize=6", HttpMethod.POST, requestEntity, typeReference, cr);
        assertNotNull(body);
        PageableResponse pageableResponse = body.getBody();
        assertNotNull(pageableResponse);
        List<Commodity> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        Assertions.assertEquals(6, responseBody.size());
        assertTrue(responseBody.get(0).getDescription().contains("Descriptio"));
    }
}
