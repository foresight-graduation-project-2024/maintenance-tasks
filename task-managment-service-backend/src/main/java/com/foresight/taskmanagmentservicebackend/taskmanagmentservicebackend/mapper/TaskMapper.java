package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.mapper;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TaskCollection;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.CreateTaskRequest;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    Task taskCollectionToTask(TaskCollection task);
    TaskCollection createTaskRequestToTaskCollection(CreateTaskRequest request);
}
