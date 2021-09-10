package com.merxport.trading.repositories;

import com.merxport.trading.entities.Commodity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommodityRepository extends MongoRepository<Commodity, String>
{
    /*@Query("{ 'name':/?0/i }")
    Page<Commodity> findByNameLikeOrderByNameAsc(String name, Sort sort, Pageable pageable);
    
    @Query("{ $or: [ { 'name':/?0/i }, { 'description':/?0/i }, {'category': {$regex:/?0/, $options: 'i' }} ] }")
    Page<Commodity> findByNameLikeOrDescriptionLikeOrCategoryLikeOrderByNameAsc(String name, Pageable pageable);*/
}
