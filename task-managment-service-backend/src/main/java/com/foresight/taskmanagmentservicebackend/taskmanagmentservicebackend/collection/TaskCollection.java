package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "task")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskCollection {
    @Id
    private String taskId;
    private String teamId;
    private String title;
    private String summary;
    private String description;
    private StatusEnum status;
    private PriorityEnum priority;
    private String label;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Member creator;
    private Member assignee;
    private List<Comment> comments;
//    private List<TaskHistory> histories;
}
