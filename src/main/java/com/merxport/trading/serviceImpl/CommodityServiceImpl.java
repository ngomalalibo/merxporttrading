package com.merxport.trading.serviceImpl;

import com.google.common.base.Strings;
import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.database.MongoConfiguration;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.services.CommodityService;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CommodityServiceImpl implements CommodityService
{
    
    @Autowired
    private MongoConfiguration mongoConfiguration;
    
    @Autowired
    private CommodityRepository commodityRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Qualifier("deleteService")
    @Autowired
    private DeleteServiceImpl deleteService;
    
    @Loggable
    @Override
    public Commodity save(Commodity c)
    {
        return commodityRepository.save(c);
    }
    
    @Loggable
    @Override
    public Commodity delete(Commodity commodity)
    {
        return deleteService.deleteEntity(commodity, mongoTemplate, commodityRepository);
    }
    
    @Override
    public List<Commodity> findCommodityByCategoryLike(String category)
    {
        return findCommodity(Criteria.where("category").regex(category, "i"), true);
    }
    
    @Override
    public List<Commodity> findCommodityByCountry(String country)
    {
        return findCommodity(Criteria.where("country").is(country), true);
    }
    
    @Override
    public List<Commodity> findCommodityByAmountGreaterThan(BigDecimal amount)
    {
        return findCommodity(Criteria.where("rate").gt(new Decimal128(amount)), true);
    }
    
    @Override
    public List<Commodity> findCommodityByAmountLessThan(BigDecimal amount)
    {
        return findCommodity(Criteria.where("rate").lt(new Decimal128(amount)), true);
    }
    
    @Override
    public List<Commodity> findCommodityByScope(Scopes scope)
    {
        return findCommodity(Criteria.where("scope").is(scope), true);
    }
    
    @Override
    public List<Commodity> findCommodityBySeller(User seller)
    {
        return findCommodity(Criteria.where("seller._id").is(new ObjectId(seller.getId())), true);
    }
    
    public List<Commodity> findCommodity(Criteria criteria, boolean isActive)
    {
        AggregationOperation operation = Aggregation.match(criteria);
        AggregationOperation operationActive = Aggregation.match(Criteria.where("isActive").is(isActive));
        List<AggregationOperation> aggPipeline = new ArrayList<>();
        aggPipeline.add(operation);
        aggPipeline.add(operationActive);
        Aggregation agg = Aggregation.newAggregation(aggPipeline);
        AggregationResults<Commodity> result = mongoTemplate.aggregate(agg, "commodities", Commodity.class);
        List<Commodity> commodities = result.getMappedResults();
        commodities.forEach(System.out::println);
        
        return commodities;
    }
    
    @Override
    public List<Commodity> findCommoditySearch(String country, String category, BigDecimal amount, Scopes scope)
    {
        Set<Commodity> result = new HashSet<>();
        AggregationOperation operation = new FacetOperation();
        Aggregation agg;
        if (!Strings.isNullOrEmpty(country))
        {
            operation = Aggregation.match(Criteria.where("country").is(country));
            agg = Aggregation.newAggregation(operation);
            AggregationResults<Commodity> commodities = mongoTemplate.aggregate(agg, "commodities", Commodity.class);
            for (Commodity com : commodities)
            {
                result.add(com);
            }
        }
        if (!Strings.isNullOrEmpty(category))
        {
            operation = Aggregation.match(Criteria.where("category").regex(category, "i"));
            agg = Aggregation.newAggregation(operation);
            AggregationResults<Commodity> commodities = mongoTemplate.aggregate(agg, "commodities", Commodity.class);
            for (Commodity com : commodities)
            {
                result.add(com);
            }
        }
        if (!Objects.isNull(category))
        {
            operation = Aggregation.match(Criteria.where("rate").gt(new Decimal128(amount)));
            agg = Aggregation.newAggregation(operation);
            AggregationResults<Commodity> commodities = mongoTemplate.aggregate(agg, "commodities", Commodity.class);
            for (Commodity com : commodities)
            {
                result.add(com);
            }
        }
        if (!Objects.isNull(scope))
        {
            operation = Aggregation.match(Criteria.where("scope").is(scope));
            agg = Aggregation.newAggregation(operation);
            AggregationResults<Commodity> commodities = mongoTemplate.aggregate(agg, "commodities", Commodity.class);
            for (Commodity com : commodities)
            {
                result.add(com);
            }
        }
        
        return new ArrayList<>(result);
    }
}
