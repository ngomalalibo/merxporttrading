package com.merxport.trading.services;

import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.response.PageableResponse;

public interface RFQService
{
    RFQ save(RFQ rfq);
    
    RFQ delete(RFQ rfq);
    
    PageableResponse findRFQByTitleLike(String title, int page, int pageSize);
    
    PageableResponse findRFQByCommodityNameLike(String name, int page, int pageSize);
    
    PageableResponse findRFQByCountry(String country, int page, int pageSize);
    
    PageableResponse findRFQByTerm(CommercialTerms term, int page, int pageSize);
}
