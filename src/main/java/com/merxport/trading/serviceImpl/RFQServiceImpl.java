package com.merxport.trading.serviceImpl;

import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.repositories.RFQRepository;
import com.merxport.trading.services.RFQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RFQServiceImpl implements RFQService
{
    @Autowired
    private RFQRepository rfqRepository;
    
    @Qualifier("deleteService")
    @Autowired
    private DeleteServiceImpl deleteService;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public RFQ save(RFQ rfq)
    {
        return rfqRepository.save(rfq);
    }
    
    @Override
    public RFQ delete(RFQ rfq)
    {
        return deleteService.deleteEntity(rfq, mongoTemplate, rfqRepository);
    }
    
    @Override
    public List<RFQ> findRFQByTitleLike(String title)
    {
        return findRFQs(Criteria.where("title").regex(title, "i"), true);
    }
    
    @Override
    public List<RFQ> findRFQByCommodityNameLike(String name)
    {
        return findRFQs(Criteria.where("commodity.name").regex(name, "i"), true);
    }
    
    @Override
    public List<RFQ> findRFQByCountry(String country)
    {
        return findRFQs(Criteria.where("country").is(country), true);
    }
    
    @Override
    public List<RFQ> findRFQByTerm(CommercialTerms term)
    {
        return findRFQs(Criteria.where("term").is(term), true);
    }
    
    public List<RFQ> findRFQs(Criteria criteria, boolean isActive)
    {
        AggregationOperation operation = Aggregation.match(criteria);
        AggregationOperation operationActive = Aggregation.match(Criteria.where("isActive").is(isActive));
        List<AggregationOperation> aggPipeline = new ArrayList<>();
        aggPipeline.add(operation);
        aggPipeline.add(operationActive);
        Aggregation agg = Aggregation.newAggregation(aggPipeline);
        AggregationResults<RFQ> result = mongoTemplate.aggregate(agg, "rfqs", RFQ.class);
        List<RFQ> rfqs = result.getMappedResults();
        rfqs.forEach(System.out::println);
        
        return rfqs;
    }
}
