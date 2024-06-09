package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.controller;


import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service.NotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/fcm")
public class FCMController {
    private final FirebaseMessaging fcm;
    private final NotificationService notificationService;

    @PostMapping("/subscriptions/{userId}")
    @Transactional
    public void createSubscription(@PathVariable("userId") String id, @RequestBody List<String> registrationTokens) throws FirebaseMessagingException, IOException {
        // Subscribe to the topic
        notificationService.subscribeToNotificationService(registrationTokens.get(0),id);

        // Check if the token is an Expo push token
        String token = registrationTokens.get(0);
        if (token.startsWith("ExponentPushToken")) {
            // Send notification via Expo's notification service
            notificationService.sendExpoNotification(registrationTokens,"Subscription","Welcome to Foresight","TASK_UPDATE");
       }

    }


    @DeleteMapping("/subscriptions/{userId}/{registrationToken}")
    public void deleteSubscription(@PathVariable("userId") String id, @PathVariable String registrationToken) throws FirebaseMessagingException {
       notificationService.unsubscribeToNotificationService(registrationToken,id);
    }
}


