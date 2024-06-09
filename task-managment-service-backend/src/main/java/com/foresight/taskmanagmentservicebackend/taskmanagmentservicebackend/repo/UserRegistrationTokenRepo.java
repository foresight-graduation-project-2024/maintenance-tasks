package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo;


import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserRegistrationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegistrationTokenRepo extends MongoRepository<UserRegistrationToken,String> {
}
