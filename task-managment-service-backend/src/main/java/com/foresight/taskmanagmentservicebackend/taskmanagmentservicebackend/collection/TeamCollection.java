package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Member;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "team")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TeamCollection {
    @Id
    private String teamId;
    private String name;
    @Indexed(unique = true)
    private String signature;
    private String description;
    private Member teamLeader;
    private List<Member> members;
    private List<Task> teamTasks;
}
