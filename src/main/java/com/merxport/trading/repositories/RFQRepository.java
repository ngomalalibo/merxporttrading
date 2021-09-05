package com.merxport.trading.repositories;

import com.merxport.trading.entities.RFQ;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RFQRepository extends MongoRepository<RFQ, String>
{

}
