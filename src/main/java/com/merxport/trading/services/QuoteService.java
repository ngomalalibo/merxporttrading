package com.merxport.trading.services;

import com.merxport.trading.entities.Quote;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.QuoteStatus;

import java.util.List;

public interface QuoteService
{
    Quote save(Quote c);
    
    Quote delete(Quote c);
    
    List<Quote> findQuoteByRFQ(RFQ rfq);
    
    Quote updateQuoteStatus(Quote quote, QuoteStatus quoteStatus);
    
    List<Quote> findQuoteByStatusAndSeller(String sellerID, QuoteStatus quoteStatus);
    
    List<Quote> findAllQuotesBySeller(String sellerID);
    
    List<Quote> findAllActive();
}
