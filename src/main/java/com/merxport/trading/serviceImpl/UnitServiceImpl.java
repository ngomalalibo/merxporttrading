package com.merxport.trading.serviceImpl;

import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.entities.Unit;
import com.merxport.trading.repositories.UnitRepository;
import com.merxport.trading.services.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class UnitServiceImpl implements UnitService
{
    @Autowired
    private UnitRepository unitRepository;
    
    @Qualifier("deleteService")
    @Autowired
    DeleteServiceImpl deleteService;
    
    @Autowired
    MongoTemplate mongoTemplate;
    
    @Loggable
    @Override
    public Unit save(Unit unit)
    {
        return unitRepository.save(unit);
    }
    
    @Loggable
    @Override
    public Unit delete(Unit unit)
    {
        return deleteService.deleteEntity(unit, mongoTemplate, unitRepository);
    }
}
