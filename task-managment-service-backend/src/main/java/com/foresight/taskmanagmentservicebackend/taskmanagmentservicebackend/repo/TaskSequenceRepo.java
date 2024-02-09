package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TaskSequence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskSequenceRepo extends MongoRepository<TaskSequence,String> {
}
