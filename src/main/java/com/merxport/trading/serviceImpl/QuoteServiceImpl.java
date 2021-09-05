package com.merxport.trading.serviceImpl;

import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.entities.Quote;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.QuoteStatus;
import com.merxport.trading.exception.CustomNullPointerException;
import com.merxport.trading.repositories.QuoteRepository;
import com.merxport.trading.services.QuoteService;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public List<Quote> findQuoteByRFQ(RFQ rfq)
    {
        return findQuote(Criteria.where("rfq._id").is(new ObjectId(rfq.getId())), true);
    }
    
    @Override
    public Quote updateQuoteStatus(Quote quote, QuoteStatus quoteStatus)
    {
        assert quote != null;
        LocalDateTime now = LocalDateTime.now();
        Query q = new Query(Criteria.where("_id").is(quote.getId()));
        Update update = new Update();
        update.set("accepted", quoteStatus);
        update.set("audit.modifiedBy", "System");
        update.set("audit.modifiedDate", now);
        UpdateResult updateResult = mongoTemplate.updateFirst(q, update, quote.getClass());
        if (updateResult.wasAcknowledged() && updateResult.getMatchedCount() == 1)
        {
            quote = quoteRepository.findById(quote.getId()).orElse(null);
            assert quote != null;
            quote.getAudit().setModifiedDate(now);
            quote.getAudit().setModifiedBy("System");
            return quote;
        }
        else
        {
            throw new CustomNullPointerException(quote.getClass().getSimpleName() + " not updated");
        }
    }
    
    @Override
    public List<Quote> findQuoteByStatusAndSeller(String sellerID, QuoteStatus quoteStatus)
    {
        return findQuote(Criteria.where("seller._id").is(new ObjectId(sellerID)).and("quoteStatus").is(quoteStatus.name()), true);
    }
    
    @Override
    public List<Quote> findAllQuotesBySeller(String sellerID)
    {
        return findQuote(Criteria.where("seller._id").is(new ObjectId(sellerID)), true);
    }
    
    @Override
    public List<Quote> findAllActive()
    {
        return findQuote(Criteria.where("isActive").is(true), true);
    }
    
    public List<Quote> findQuote(Criteria criteria, boolean isActive)
    {
        AggregationOperation operation = Aggregation.match(criteria);
        AggregationOperation operationActive = Aggregation.match(Criteria.where("isActive").is(isActive));
        List<AggregationOperation> aggPipeline = new ArrayList<>();
        aggPipeline.add(operation);
        aggPipeline.add(operationActive);
        Aggregation agg = Aggregation.newAggregation(aggPipeline);
        AggregationResults<Quote> result = mongoTemplate.aggregate(agg, "quotes", Quote.class);
        List<Quote> quotes = result.getMappedResults();
        quotes.forEach(System.out::println);
        
        return quotes;
    }
}
