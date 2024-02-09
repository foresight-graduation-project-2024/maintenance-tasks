package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepo extends MongoRepository<UserInfo,String>
{
    Optional<UserInfo> findByUserId(Long id);
    Optional<UserInfo> findByEmail(String email);

}
