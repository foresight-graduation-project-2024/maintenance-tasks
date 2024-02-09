package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateTaskRequest {
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
