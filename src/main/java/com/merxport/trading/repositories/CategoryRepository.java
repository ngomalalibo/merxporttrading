package com.merxport.trading.repositories;

import com.merxport.trading.entities.CommodityCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<CommodityCategory, String>
{
    List<CommodityCategory> findByNameLikeOrderByNameAsc(String name);
}
