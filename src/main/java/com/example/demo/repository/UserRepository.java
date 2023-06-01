package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


//connect to mongodb
//repo auto wiring is at AuthController class
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findById(String userId);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    void deleteByUsername(String username);

}
