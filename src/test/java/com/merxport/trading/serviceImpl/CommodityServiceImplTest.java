package com.merxport.trading.serviceImpl;

import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;
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
                                            new BigDecimal(100000), faker.number().numberBetween(1,50), faker.stock().nsdqSymbol(), user,
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
        List<Commodity> commodities = commodityService.findCommodityByCategoryLike("u");
        assertEquals(3, commodities.size());
        assertEquals("Publishing", commodities.get(0).getCategory().get(0));
    }
    
    @Test
    void findCommodityByCountry()
    {
        List<Commodity> commodities = commodityService.findCommodityByCountry("Iceland");
        assertEquals(1, commodities.size());
        assertEquals("Iceland", commodities.get(0).getCountry());
    }
    
    @Test
    void findCommodityByAmountGreaterThan()
    {
        List<Commodity> commodities = commodityService.findCommodityByAmountGreaterThan(new BigDecimal(30000));
        assertEquals(6, commodities.size());
        assertEquals(new BigDecimal(100000), commodities.get(0).getRate());
    }
    
    @Test
    void findCommodityByAmountLessThan()
    {
        List<Commodity> commodities = commodityService.findCommodityByAmountLessThan(new BigDecimal(40000));
        assertEquals(1, commodities.size());
        assertEquals(new BigDecimal(30000), commodities.get(0).getRate());
    }
    
    @Test
    void findCommodityByScope()
    {
        List<Commodity> commodities = commodityService.findCommodityByScope(Scopes.INTERNATIONAL);
        assertEquals(7, commodities.size());
        assertEquals("INTERNATIONAL", commodities.get(0).getScope().name());
    }
    
    @Test
    void findCommodityBySSeller()
    {
        String sellerId = "6126a4897f80646d7836a051";
        User user = new User();
        user.setId(sellerId);
        List<Commodity> commodities = commodityService.findCommodityBySeller(user);
        assertEquals(7, commodities.size());
        assertEquals("Synergistic Cotton Computer", commodities.get(0).getName());
    }
    
    @Test
    void findCommoditySearch()
    {
        List<Commodity> commodityFacets = commodityService.findCommoditySearch("Iceland", "Services", new BigDecimal(40000), Scopes.INTERNATIONAL);
        assertEquals(6, commodityFacets.size());
        assertTrue(commodityFacets.get(0).getDescription().contains("Descriptio"));
        
    }
    
    public static void main(String[] args)
    {
        System.out.println(new BigDecimal(40000).equals(new Decimal128(40000).bigDecimalValue()));
    }
}
