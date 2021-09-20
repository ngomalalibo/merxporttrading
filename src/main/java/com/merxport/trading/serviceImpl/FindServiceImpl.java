package com.merxport.trading.serviceImpl;

import com.merxport.trading.entities.PersistingBaseEntity;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.response.PageableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class FindServiceImpl
{
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Value("${pagination.page-size}")
    private int pageSize;
    
    public <T extends PersistingBaseEntity> PageableResponse find(T t, String collection, Criteria criteria, boolean isActive, int pageSize, long page, Sort sort)
    {
        
        
        AggregationOperation operation = Aggregation.match(criteria);
        AggregationOperation operationActive = Aggregation.match(Criteria.where("isActive").is(isActive));
        CountOperation operationCount = Aggregation.count().as("totalItems");
        List<AggregationOperation> aggPipeline = new LinkedList<>();
        aggPipeline.add(operation);
        aggPipeline.add(operationActive);
        aggPipeline.add(operationCount);
        
        PageableResponse pageableResponse = new PageableResponse();
        long totalItems = 0;
        pageableResponse.setCurrentPage(page);
        
        Aggregation agg = Aggregation.newAggregation(aggPipeline);
        Map countResult = mongoTemplate.aggregate(agg, collection, Map.class).getUniqueMappedResult();
        if (null != countResult && countResult.containsKey("totalItems"))
        {
            totalItems = Long.valueOf(countResult.get("totalItems").toString());
            pageableResponse.setTotalItems(totalItems);
        }
        long totalPages = (long) Math.ceil(totalItems / pageSize);
        pageableResponse.setTotalPages(totalPages == 0 ? 1 : totalPages);
        
        if (page > pageableResponse.getTotalPages())
        {
            throw new EntityNotFoundException("Page not found");
        }
        
        aggPipeline = new LinkedList<>();
        long offset = page == 1 ? 0 : page * pageSize;
        AggregationOperation operationOffset = Aggregation.skip(offset);
        AggregationOperation operationLimit = Aggregation.limit(pageSize);
        AggregationOperation operationSort = Aggregation.sort(sort);
        aggPipeline.add(operation);
        aggPipeline.add(operationActive);
        aggPipeline.add(operationOffset);
        aggPipeline.add(operationSort);
        aggPipeline.add(operationLimit);
        agg = Aggregation.newAggregation(aggPipeline);
        AggregationResults result = mongoTemplate.aggregate(agg, collection, t.getClass());
        List entity = result.getMappedResults();
        pageableResponse.setResponseBody(entity);
        return pageableResponse;
    }
}
