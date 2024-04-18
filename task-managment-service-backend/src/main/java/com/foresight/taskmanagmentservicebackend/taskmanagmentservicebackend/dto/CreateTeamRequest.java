package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeamRequest {
    @NotEmpty
    @NotNull
    private String name;
    @NotEmpty
    @NotNull
    private String signature;
    @NotEmpty
    @NotNull
    private String description;
    @NotNull
    private Member teamLeader;
    @NotNull
    private List<Member> members;
}
