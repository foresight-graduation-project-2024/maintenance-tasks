package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.controller;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.User;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Task;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Team;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@CrossOrigin

public class UserController {
    UserService userService;
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id){
        return userService.getUser(id);
    }
    @GetMapping("/teams/{userId}")
    public Page<Team> userTeams(Pageable pageable,@PathVariable String userId){
        return userService.getUserTeams(pageable,userId);
    }

    @GetMapping("/tasks/{userId}")
    public Page<Task> userTasks(Pageable pageable,@PathVariable String userId){
        return userService.getUserTasks(pageable,userId);
    }
}
