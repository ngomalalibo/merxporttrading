package com.merxport.trading.repositories;

import com.merxport.trading.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>
{
    @Query("{'isActive':?0}")
    Optional<List<User>> findUsers(boolean isActive, Sort sort);
}
