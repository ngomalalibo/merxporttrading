package com.merxport.trading.serviceImpl;

import com.google.common.base.Strings;
import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.database.MongoConfiguration;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.response.PageableResponse;
import com.merxport.trading.services.CommodityService;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    
    @Autowired
    private FindServiceImpl findService;
    
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
    public PageableResponse findCommodityByCategoryLike(String category, int page, int pageSize)
    {
        return findCommodity(Criteria.where("category").regex(category, "i"), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findCommodityByCountry(String country, int page, int pageSize)
    {
        return findCommodity(Criteria.where("country").is(country), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findCommodityByAmountGreaterThan(BigDecimal amount, int page, int pageSize)
    {
        return findCommodity(Criteria.where("rate").gt(new Decimal128(amount)), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findCommodityByAmountLessThan(BigDecimal amount, int page, int pageSize)
    {
        return findCommodity(Criteria.where("rate").lt(new Decimal128(amount)), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findCommodityByScope(Scopes scope, int page, int pageSize)
    {
        return findCommodity(Criteria.where("scope").is(scope), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findCommodityBySeller(User seller, int page, int pageSize)
    {
        return findCommodity(Criteria.where("seller._id").is(new ObjectId(seller.getId())), true, page, pageSize);
    }
    
    public PageableResponse findCommodity(Criteria criteria, boolean isActive, int page, int pageSize)
    {
        return findService.find(new Commodity(), "commodities", criteria, isActive, pageSize, page, Sort.by(Sort.Direction.ASC, "name"));
        /*AggregationOperation operation = Aggregation.match(criteria);
        AggregationOperation operationActive = Aggregation.match(Criteria.where("isActive").is(isActive));
        List<AggregationOperation> aggPipeline = new ArrayList<>();
        aggPipeline.add(operation);
        aggPipeline.add(operationActive);
        Aggregation agg = Aggregation.newAggregation(aggPipeline);
        AggregationResults<Commodity> result = mongoTemplate.aggregate(agg, "commodities", Commodity.class);
        List<Commodity> commodities = result.getMappedResults();
        commodities.forEach(System.out::println);
        
        return commodities;*/
    }
    
    @Override
    public PageableResponse findCommoditySearch(String country, String category, BigDecimal amount, Scopes scope, int page, int pageSize)
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
        ArrayList<Commodity> commodities = new ArrayList<>(result);
        PageableResponse pr = new PageableResponse();
        pr.setCurrentPage(page);
        int totalSize = commodities.size();
        pr.setTotalItems(totalSize);
        int offset = page == 1 ? 0 : page * pageSize;
        if (offset < totalSize && (offset + pageSize) < totalSize)
        {
            pr.setResponseBody(commodities.subList(offset, offset + pageSize));
            
        }
        pr.setTotalPages((long) Math.ceil(totalSize / pageSize));
        
        return pr;
    }
    
    @Override
    public PageableResponse findByNameLikeOrderByNameAsc(String name, int page, int pageSize)
    {
        return findService.find(new Commodity(), "commodities", Criteria.where("name").regex(name, "i"), true, pageSize, page, Sort.by(Sort.Direction.ASC, "name"));
    }
    
    @Override
    public PageableResponse findByNameLikeOrDescriptionLikeOrCategoryLikeOrderByNameAsc(String search, int page, int pageSize)
    {
        Criteria criteria = new Criteria().orOperator(Criteria.where("name").regex(search, "i"), Criteria.where("description").regex(search, "i"), Criteria.where("category").regex(search, "i"));
        return findService.find(new Commodity(), "commodities", criteria, true, pageSize, page, Sort.by(Sort.Direction.ASC, "name"));
    }
}
