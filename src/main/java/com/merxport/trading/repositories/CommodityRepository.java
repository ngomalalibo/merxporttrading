package com.merxport.trading.repositories;

import com.merxport.trading.entities.Commodity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommodityRepository extends MongoRepository<Commodity, String>
{
    List<Commodity> findByNameLikeOrderByNameAsc(String name);
    
    @Query("{ $or: [ { 'name':/?0/i }, { 'description':/?0/i } ] }")
    List<Commodity> findByNameLikeOrDescriptionLikeOrCategoryLikeOrderByNameAsc(String name);
    
    // @Query("{ $or: [ { 'name': {$regex:/?0/i} }, { 'description':{$regex: /?0/i} }, { 'category':{$regex: [/?0/i]} } ] }")
    // @Query("{ $or: [ { 'category':/?0/i } ] }")
    // List<Commodity> findByNameLikeOrDescriptionLikeOrCategoryLikeOrderByNameAsc(String name);
    
    // @Query("{ $or: [ { 'name':/?0/i}, { 'description':/?0/i} ] }")
    // List<Commodity> findByNameLikeOrDescriptionLikeOrCategoryLikeOrderByNameAsc(String name);
}
