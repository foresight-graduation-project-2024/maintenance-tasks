package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TeamCollection;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.TeamSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TeamCollectionRepo extends MongoRepository<TeamCollection,String> {
}
