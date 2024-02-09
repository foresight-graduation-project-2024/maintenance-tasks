package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.controller;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.User;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserInfo;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service.UserInfoService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userInfo")
@RequiredArgsConstructor
@CrossOrigin
public class UserInfoController {
    private final UserInfoService userInfoService;

    @GetMapping("/{email}")
    public UserInfo getUser(@PathVariable String email){
        return userInfoService.getUserByEmail(email);
    }

}
