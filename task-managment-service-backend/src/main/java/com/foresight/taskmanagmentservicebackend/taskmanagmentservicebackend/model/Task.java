package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String taskId;
    private String title;
//    private String description;
    private String summary;
    private StatusEnum Status;
    private PriorityEnum priority;
//    private String label;
//    private LocalDateTime startDate;
//    private LocalDateTime endDate;
//    private Member creator;
    private Member assignee;
}
