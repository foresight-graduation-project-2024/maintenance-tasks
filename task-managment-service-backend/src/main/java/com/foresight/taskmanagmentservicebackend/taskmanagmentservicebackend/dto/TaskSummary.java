package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummary {
    @Id
    private String taskId;
    private String title;
    private StatusEnum status;
    private PriorityEnum priority;
    private String label;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Member creator;
}
