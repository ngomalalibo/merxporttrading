package com.merxport.trading.repositories;

import com.merxport.trading.entities.Unit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends MongoRepository<Unit, String>
{
    List<Unit> findBySingularNameLikeOrderBySingularNameAsc(String singularName);
}
