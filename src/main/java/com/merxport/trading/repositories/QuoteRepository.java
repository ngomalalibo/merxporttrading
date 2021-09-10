package com.merxport.trading.repositories;

import com.merxport.trading.entities.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends MongoRepository<Quote, String>
{
    Page<Quote> findAll(Pageable pageable);
}
