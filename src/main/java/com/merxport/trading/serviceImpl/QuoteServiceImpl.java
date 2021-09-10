package com.merxport.trading.serviceImpl;

import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.entities.Quote;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.QuoteStatus;
import com.merxport.trading.exception.CustomNullPointerException;
import com.merxport.trading.repositories.QuoteRepository;
import com.merxport.trading.response.PageableResponse;
import com.merxport.trading.services.QuoteService;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QuoteServiceImpl implements QuoteService
{
    @Autowired
    private QuoteRepository quoteRepository;
    
    @Qualifier("deleteService")
    @Autowired
    private DeleteServiceImpl deleteService;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private FindServiceImpl findService;
    
    @Loggable
    @Override
    public Quote save(Quote Q)
    {
        return quoteRepository.save(Q);
    }
    
    @Loggable
    @Override
    public Quote delete(Quote q)
    {
        return deleteService.deleteEntity(q, mongoTemplate, quoteRepository);
    }
    
    @Override
    public PageableResponse findQuoteByRFQ(RFQ rfq, int page, int pageSize)
    {
        return findQuote(Criteria.where("rfq._id").is(new ObjectId(rfq.getId())), true, page, pageSize);
    }
    
    @Override
    public Quote updateQuoteStatus(Quote quote, QuoteStatus quoteStatus)
    {
        assert quote != null;
        LocalDateTime now = LocalDateTime.now();
        Query q = new Query(Criteria.where("_id").is(quote.getId()));
        Update update = new Update();
        update.set("accepted", quoteStatus);
        update.set("audit.modifiedBy", quote.getSessionUser());
        update.set("audit.modifiedDate", now);
        UpdateResult updateResult = mongoTemplate.updateFirst(q, update, quote.getClass());
        if (updateResult.wasAcknowledged() && updateResult.getMatchedCount() == 1)
        {
            quote = quoteRepository.findById(quote.getId()).orElse(null);
            assert quote != null;
            quote.getAudit().setModifiedDate(now);
            quote.getAudit().setModifiedBy(quote.getSessionUser());
            return quote;
        }
        else
        {
            throw new CustomNullPointerException(quote.getClass().getSimpleName() + " not updated");
        }
    }
    
    @Override
    public PageableResponse findQuoteByStatusAndSeller(String sellerID, QuoteStatus quoteStatus, int page, int pageSize)
    {
        return findQuote(Criteria.where("seller._id").is(new ObjectId(sellerID)).and("quoteStatus").is(quoteStatus.name()), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findAllQuotesBySeller(String sellerID, int page, int pageSize)
    {
        return findQuote(Criteria.where("seller._id").is(new ObjectId(sellerID)), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findAllActive(int page, int pageSize)
    {
        // return findQuote(Criteria.where("isActive").is(true), true);
        return findQuote(Criteria.where("isActive").is(true), true, page, pageSize);
    }
    
    public PageableResponse findQuote(Criteria criteria, boolean isActive, int page, int pageSize)
    {
        return findService.find(new Quote(), "quotes", criteria, isActive, pageSize, page, Sort.by(Sort.Direction.ASC, "audit.createdDate"));
        
        /*AggregationOperation operation = Aggregation.match(criteria);
        AggregationOperation operationActive = Aggregation.match(Criteria.where("isActive").is(isActive));
        List<AggregationOperation> aggPipeline = new ArrayList<>();
        aggPipeline.add(operation);
        aggPipeline.add(operationActive);
        Aggregation agg = Aggregation.newAggregation(aggPipeline);
        AggregationResults<Quote> result = mongoTemplate.aggregate(agg, "quotes", Quote.class);
        List<Quote> quotes = result.getMappedResults();
        quotes.forEach(System.out::println);
        
        return quotes;
         */
    }
}
