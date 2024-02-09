package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    private String teamId;
    private String name;
    private String signature;
    private Member teamLeader;
    private List<Member> members;
    private String description;
}
