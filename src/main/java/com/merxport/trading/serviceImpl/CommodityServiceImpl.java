package com.merxport.trading.serviceImpl;

import com.google.common.base.Strings;
import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.User;
import com.merxport.trading.exception.CustomNullPointerException;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.services.CommodityService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CommodityServiceImpl implements CommodityService
{
    @Autowired
    private CommodityRepository commodityRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private DeleteServiceImpl deleteService;
    
    @Loggable
    @Override
    public Commodity deleteCommodity(String id)
    {
        if (Strings.isNullOrEmpty(id))
        {
            throw new EntityNotFoundException("Provide a valid ID");
        }
        Commodity commodity = commodityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Commodity not found"));
    
        return deleteService.deleteEntity(commodity, mongoTemplate, commodityRepository);
    }
}
