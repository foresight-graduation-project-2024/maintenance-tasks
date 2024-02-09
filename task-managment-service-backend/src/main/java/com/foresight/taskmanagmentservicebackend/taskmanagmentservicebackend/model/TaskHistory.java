package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistory {
    private String id;
    private String description;
    private LocalDateTime issuedDate;
    private Member actor;
}
