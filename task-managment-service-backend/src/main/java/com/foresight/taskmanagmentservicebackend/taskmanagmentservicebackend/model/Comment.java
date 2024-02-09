package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String id;
    private String content;
    private LocalDateTime issuedDate;
    private Member owner;
    private List<Reply> replies;
}
