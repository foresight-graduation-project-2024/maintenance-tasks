package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamSearchCriteria {
    private String name;
    private String LeaderFirstname;
    private String LeaderLastname;
    private String LeaderRole;
}
