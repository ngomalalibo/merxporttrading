package com.merxport.trading.serviceImpl;

import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.entities.Unit;
import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.enumerations.RFQPriority;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.repositories.RFQRepository;
import com.merxport.trading.repositories.UnitRepository;
import com.merxport.trading.services.CommodityService;
import com.merxport.trading.services.RFQService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    void findRFQByTitleLike()
    {
        List<RFQ> rfqs = rfqService.findRFQByTitleLike("Granit");
        assertEquals(1, rfqs.size());
        assertEquals("Gorgeous Granite Gloves", rfqs.get(0).getTitle());
    }
    
    @Test
    void findRFQByCommodityNameLike()
    {
        String name = "Ergonomic";
        List<RFQ> rfqs = rfqService.findRFQByCommodityNameLike(name);
        assertEquals(1, rfqs.size());
        assertEquals("Ergonomic Cotton Lamp", rfqs.get(0).getCommodity().getName());
    }
    
    @Test
    void findRFQByCountry()
    {
        String country = "Nigeria";
        List<RFQ> rfqs = rfqService.findRFQByCountry(country);
        assertEquals(1, rfqs.size());
        assertEquals("Nigeria", rfqs.get(0).getCountry());
    }
    
    @Test
    void findRFQByTerm()
    {
        CommercialTerms term = CommercialTerms.COST_INSURANCE_AND_FREIGHT;
        List<RFQ> rfqs = rfqService.findRFQByTerm(term);
        assertEquals(1, rfqs.size());
        assertEquals("COST_INSURANCE_AND_FREIGHT", rfqs.get(0).getTerm().name());
    }
}
