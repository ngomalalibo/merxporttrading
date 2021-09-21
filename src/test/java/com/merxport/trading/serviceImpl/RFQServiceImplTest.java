package com.merxport.trading.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.entities.Unit;
import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.enumerations.RFQPriority;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.repositories.RFQRepository;
import com.merxport.trading.repositories.UnitRepository;
import com.merxport.trading.response.PageableResponse;
import com.merxport.trading.services.CommodityService;
import com.merxport.trading.services.RFQService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RFQServiceImplTest extends AbstractIntegrationTest
{
    @Autowired
    private RFQService rfqService;
    
    @Autowired
    private RFQRepository rfqRepository;
    
    @Autowired
    private CommodityService commodityService;
    
    @Autowired
    private CommodityRepository commodityRepository;
    
    @Autowired
    private UnitRepository unitRepository;
    
    @Qualifier("getObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    
    private TypeReference<List<RFQ>> typeReferenceList = new TypeReference<>()
    {
    };
    
    public RFQ getRFQ()
    {
        String imageID = "6126a4817f80646d7836a04f";
        String unitID = "61325d5dfce8ee74deff5415";
        Unit unit = unitRepository.findById(unitID).orElseThrow(EntityNotFoundException::new);
        Currency naira = Currency.getInstance("NGN");
        return new RFQ(faker.commerce().productName(),
                       "6130ff9f4ed20e41b43a503c",
                       new BigDecimal(20000), true, unit.getSingularName(),
                       faker.number().numberBetween(1, 100),
                       null, "QCDoc", RFQPriority.MEDIUM,
                       faker.number().numberBetween(10, 20),
                       faker.number().numberBetween(99, 100), LocalDateTime.now(),
                       naira, imageID, null, "Nigeria", CommercialTerms.COST_INSURANCE_AND_FREIGHT, "Lagos", "Good", "6126806273aade16270429c4", new BigDecimal(300000), new BigDecimal(350000));
    }
    
    
    @Test
    void save()
    {
        RFQ rfq = getRFQ();
        RFQ saved = rfqService.save(rfq);
        assertEquals(rfq.getLocation(), saved.getLocation());
        assertNotNull(saved.getId());
    }
    
    @Test
    void delete()
    {
        String id = "6132233e12e8966bf50d04eb";
        RFQ deleted = rfqService.delete(rfqRepository.findById(id).orElse(null));
        assertNotNull(deleted);
        assertFalse(deleted.isActive());
    }
    
    @Test
    void findAll()
    {
        PageableResponse pageableResponse = rfqService.findAll( 0, 10);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(9  , rfqs.size());
        assertEquals("Gorgeous Granite Gloves", rfqs.get(0).getTitle());
    }
    
    @Test
    void findRFQByTitleLike()
    {
        PageableResponse pageableResponse = rfqService.findRFQByTitleLike("Granit", 0, 6);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(1, rfqs.size());
        assertEquals("Gorgeous Granite Gloves", rfqs.get(0).getTitle());
    }
    
    @Test
    void findRFQByCommodityNameLike()
    {
        String name = "Ergonomic";
        PageableResponse pageableResponse = rfqService.findRFQByCommodityNameLike(name, 0, 6);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(1, rfqs.size());
        assertEquals("6130ff9f4ed20e41b43a503c", rfqs.get(0).getCommodityID());
    }
    
    @Test
    void findRFQByCountry()
    {
        String country = "Nigeria";
        PageableResponse pageableResponse = rfqService.findRFQByCountry(country, 0, 6);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(1, rfqs.size());
        assertEquals("Nigeria", rfqs.get(0).getCountry());
    }
    
    @Test
    void findRFQByTerm()
    {
        CommercialTerms term = CommercialTerms.COST_INSURANCE_AND_FREIGHT;
        PageableResponse pageableResponse = rfqService.findRFQByTerm(term, 0, 6);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(1, rfqs.size());
        assertEquals("COST_INSURANCE_AND_FREIGHT", rfqs.get(0).getTerm().name());
    }
    
    @Test
    void findRFQByBuyerID()
    {
        String buyerID = "6126806273aade16270429c4";
        PageableResponse pageableResponse = rfqService.findRFQByBuyer(buyerID, 0, 6);
        List<RFQ> rfqs = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList);
        assertEquals(1, rfqs.size());
        assertEquals("Enormous Silk Knife", rfqs.get(0).getTitle());
    }
    
}
