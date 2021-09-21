package com.merxport.trading.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.RFQRepository;
import com.merxport.trading.response.PageableResponse;
import com.merxport.trading.services.RFQService;
import com.merxport.trading.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

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
    
    @Autowired
    private FindServiceImpl findService;
    
    @Qualifier("getObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    
   @Autowired
    private UserService userService;
    
    private TypeReference<List<RFQ>> typeReferenceList = new TypeReference<>()
    {
    };
    
    @Loggable
    @Override
    public RFQ save(RFQ rfq)
    {
        return rfqRepository.save(rfq);
    }
    
    @Loggable
    @Override
    public RFQ delete(RFQ rfq)
    {
        return deleteService.deleteEntity(rfq, mongoTemplate, rfqRepository);
    }
    
    @Override
    public PageableResponse findAll(int page, int pageSize)
    {
        return findRFQs(new Criteria(), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findRFQByTitleLike(String title, int page, int pageSize)
    {
        return findRFQs(Criteria.where("title").regex(title, "i"), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findRFQByCommodityNameLike(String name, int page, int pageSize)
    {
        return findRFQs(Criteria.where("commodity.name").regex(name, "i"), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findRFQByCountry(String country, int page, int pageSize)
    {
        return findRFQs(Criteria.where("country").is(country), true, page, pageSize);
    }
    
    @Override
    public PageableResponse findRFQByTerm(CommercialTerms term, int page, int pageSize)
    {
        return findRFQs(Criteria.where("term").is(term), true, page, pageSize);
    }
    
    @Override
    public RFQ findByID(String id)
    {
        RFQ rfq = rfqRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        
        try
        {
            rfq.setSampleImage(userService.getImage(rfq.getSampleImageID(), 0, 0, "JPEG"));
            return rfq;
        }
        catch (Exception ex)
        {
            throw new EntityNotFoundException("RFQ not found");
        }
    }
    
    public PageableResponse findRFQs(Criteria criteria, boolean isActive, int page, int pageSize)
    {
        /**populate rfq with image from image id*/
        PageableResponse pageableResponse = findService.find(new RFQ(), "rfqs", criteria, isActive, pageSize, page, Sort.by(Sort.Direction.ASC, "audit.createdDate"));
        List<RFQ> responseBody = objectMapper.convertValue(pageableResponse.getResponseBody(), typeReferenceList)
                                             .stream().peek(rfq ->
                                                            {
                                                                try
                                                                {
                                                                    rfq.setSampleImage(userService.getImage(rfq.getSampleImageID(), 0, 0, "JPEG"));
                                                                }
                                                                catch (Exception ex)
                                                                {
                                                                    throw new EntityNotFoundException("RFQ not found");
                                                                }
                                                            }).collect(Collectors.toList());
        // .stream().peek(rfq -> rfq.setSampleImage(restTemplate.getForEntity("https://merxporttrading.herokuapp.com/getImage/{id}", String.class, rfq.getSampleImageID()).getBody())).collect(Collectors.toList());
        pageableResponse.setResponseBody(responseBody);
        return pageableResponse;
        /**return rfq with image id*/
        // return findService.find(new RFQ(), "rfqs", criteria, isActive, pageSize, page, Sort.by(Sort.Direction.ASC, "audit.createdDate"));
        /*AggregationOperation operation = Aggregation.match(criteria);
        AggregationOperation operationActive = Aggregation.match(Criteria.where("isActive").is(isActive));
        List<AggregationOperation> aggPipeline = new ArrayList<>();
        aggPipeline.add(operation);
        aggPipeline.add(operationActive);
        Aggregation agg = Aggregation.newAggregation(aggPipeline);
        AggregationResults<RFQ> result = mongoTemplate.aggregate(agg, "rfqs", RFQ.class);
        List<RFQ> rfqs = result.getMappedResults();
        rfqs.forEach(System.out::println);
        
        return rfqs;*/
    }
    
    @Override
    public PageableResponse findRFQByBuyer(String id, int page, int pageSize)
    {
        return findRFQs(Criteria.where("buyerID").is(id), true, page, pageSize);
    }
}
