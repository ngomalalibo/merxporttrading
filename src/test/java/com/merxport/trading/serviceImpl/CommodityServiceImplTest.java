package com.merxport.trading.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.response.PageableResponse;
import com.merxport.trading.services.CommodityService;
import com.merxport.trading.services.UserService;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommodityServiceImplTest extends AbstractIntegrationTest
{
    @Autowired
    private CommodityService commodityService;
    
    @Autowired
    private UserService userService;
    private TypeReference<List<Commodity>> typeReferenceList = new TypeReference<>()
    {
    };
    
    @Test
    void save() throws IOException
    {
        String imageID = "6126a4817f80646d7836a04f";
        User user = userService.findUser("6126a4897f80646d7836a051");
        Commodity commodity = new Commodity(faker.commerce().productName(),
                                            Collections.singletonList(faker.company().industry()),
                                            faker.commerce().productName(), new HashMap<>(),
                                            faker.book().title(),
                                            Collections.singletonList(imageID),
                                            new BigDecimal(100000), faker.number().numberBetween(1, 50), faker.stock().nsdqSymbol(), user.getId(),
                                            faker.country().name(),
                                            Scopes.INTERNATIONAL);
        Commodity save = commodityService.save(commodity);
        assertNotNull(save);
        assertNotNull(save.getId());
        
    }
    
    @Test
    void delete()
    {
    }
    
    @Test
    void findCommodityByCategoryLike()
    {
        PageableResponse pageableResponse = commodityService.findCommodityByCategoryLike("u", 0, 6);
        List<Commodity> commodities = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(3, commodities.size());
        assertEquals("Publishing", commodities.get(0).getCategory().get(0));
    }
    
    @Test
    void findCommodityByCountry()
    {
        PageableResponse pageableResponse = commodityService.findCommodityByCountry("Iceland", 0, 6);
        List<Commodity> commodities = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(1, commodities.size());
        assertEquals("Iceland", commodities.get(0).getCountry());
    }
    
    @Test
    void findCommodityByAmountGreaterThan()
    {
        PageableResponse pageableResponse = commodityService.findCommodityByAmountGreaterThan(new BigDecimal(30000), 0, 6);
        List<Commodity> commodities = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(6, commodities.size());
        assertEquals(new BigDecimal(100000), commodities.get(0).getRate());
    }
    
    @Test
    void findCommodityByAmountLessThan()
    {
        PageableResponse pageableResponse = commodityService.findCommodityByAmountLessThan(new BigDecimal(40000), 0, 6);
        List<Commodity> commodities = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(1, commodities.size());
        assertEquals(new BigDecimal(30000), commodities.get(0).getRate());
    }
    
    @Test
    void findCommodityByScope()
    {
        PageableResponse pageableResponse = commodityService.findCommodityByScope(Scopes.INTERNATIONAL, 0, 6);
        List<Commodity> commodities = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(6, commodities.size());
        // assertEquals(7, commodities.size());
        assertEquals("INTERNATIONAL", commodities.get(0).getScope().name());
    }
    
    @Test
    void findCommodityBySSeller()
    {
        String sellerId = "6126a4897f80646d7836a051";
        User user = new User();
        user.setId(sellerId);
        PageableResponse pageableResponse = commodityService.findCommodityBySeller(user, 0, 6);
        List<Commodity> commodities = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(7, commodities.size());
        assertEquals("Synergistic Cotton Computer", commodities.get(0).getName());
    }
    
    @Test
    void findCommoditySearch()
    {
        PageableResponse pageableResponse = commodityService.findCommoditySearch("Iceland", "Services", new BigDecimal(40000), Scopes.INTERNATIONAL, 0, 6);
        List<Commodity> commodities = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(6, commodities.size());
        assertTrue(commodities.get(0).getDescription().contains("Descriptio"));
        
    }
    
    public static void main(String[] args)
    {
        // System.out.println(new BigDecimal(40000).equals(new Decimal128(40000).bigDecimalValue()));
        System.out.println(new CommodityServiceImplTest().faker.internet().password().substring(0,8));
    }
}
