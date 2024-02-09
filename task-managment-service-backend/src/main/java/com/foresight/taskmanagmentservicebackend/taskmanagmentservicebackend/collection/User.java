package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Task;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class User {
    @Id
    private String userId;
    private List<Task> tasks;
    private List<Team> teams;
}
