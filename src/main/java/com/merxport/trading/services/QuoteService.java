package com.merxport.trading.services;

import com.merxport.trading.entities.Quote;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.QuoteStatus;
import com.merxport.trading.response.PageableResponse;

public interface QuoteService
{
    Quote save(Quote c);
    
    Quote delete(Quote c);
    
    PageableResponse findQuoteByRFQ(RFQ rfq, int page, int pageSize);
    
    Quote updateQuoteStatus(Quote quote, QuoteStatus quoteStatus);
    
    PageableResponse findQuoteByStatusAndSeller(String sellerID, QuoteStatus quoteStatus, int page, int pageSize);
    
    PageableResponse findAllQuotesBySeller(String sellerID, int page, int pageSize);
    
    PageableResponse findAllActive(int page, int pageSize);
}
