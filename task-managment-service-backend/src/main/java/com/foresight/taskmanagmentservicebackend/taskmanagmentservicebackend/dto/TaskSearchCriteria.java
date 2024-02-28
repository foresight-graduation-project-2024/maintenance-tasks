package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.PriorityEnum;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSearchCriteria {
    private String title;
    private StatusEnum status;
    private PriorityEnum priority;
//    private String label;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String creatorFirstname;
    private String creatorLastname;
    private String assigneeFirstname;
    private String assigneeLastname;
}
