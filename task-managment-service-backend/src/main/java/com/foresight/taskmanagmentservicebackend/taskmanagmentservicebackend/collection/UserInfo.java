package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usersInfo")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserInfo {


    @Id
    private String id;
    @Indexed(unique = true)
    private Long userId;
    private String firstname;
    private String lastname;
    @Indexed(unique = true)
    private String email;
    private String role;
    private boolean enabled;
}
