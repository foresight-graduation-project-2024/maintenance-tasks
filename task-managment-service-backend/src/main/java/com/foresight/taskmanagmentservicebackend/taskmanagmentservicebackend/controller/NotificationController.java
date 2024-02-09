package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.controller;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.User;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserNotifications;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Notification;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/notification")
@CrossOrigin

public class NotificationController {
    private NotificationService notificationService;
    //for testing
    @GetMapping("/getAll")
    public List<UserNotifications> getAllNotification(){
        return notificationService.getAllUsersNotifications();
    }
    @GetMapping("/{id}")
    public Page<Notification> getUserNotifications(Pageable pageable,@PathVariable String id){
        return notificationService.getUserNotifications(pageable,id);
    }
}
