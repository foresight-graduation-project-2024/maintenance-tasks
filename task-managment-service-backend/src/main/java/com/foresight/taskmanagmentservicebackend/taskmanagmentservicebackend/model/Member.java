package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private String memberId;
    private String firstname;
    private String lastname;
    private String email;
    private String role;
}
