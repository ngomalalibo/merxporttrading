package com.merxport.trading.services;

import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.CommercialTerms;

import java.util.List;

public interface RFQService
{
    RFQ save(RFQ rfq);
    
    RFQ delete(RFQ rfq);
    
    List<RFQ> findRFQByTitleLike(String title);
    
    List<RFQ> findRFQByCommodityNameLike(String name);
    
    List<RFQ> findRFQByCountry(String country);
    
    List<RFQ> findRFQByTerm(CommercialTerms term);
}
