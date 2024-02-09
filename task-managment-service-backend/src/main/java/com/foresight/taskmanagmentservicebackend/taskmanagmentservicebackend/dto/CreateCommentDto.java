package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDto
{
    private String content;
    private Long ownerId;
}
