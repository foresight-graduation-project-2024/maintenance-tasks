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
    public void createSubscription(@PathVariable("userId") String id, @RequestBody String registrationToken) throws FirebaseMessagingException, IOException {
        // Subscribe to the topic
        notificationService.subscribeToNotificationService(registrationToken,id);

        // Check if the token is an Expo push token
        List<String> tokens= new ArrayList<String>();
        tokens.add(registrationToken);
        if (registrationToken.startsWith("ExponentPushToken")) {
            // Send notification via Expo's notification service
            notificationService.sendExpoNotification(tokens,"Subscription","Welcome to Foresight","TASK_UPDATE");
        } else {
            // Send notification via FCM
            Message msg = Message.builder()
                    .putData("notificationType", "TASK_UPDATE")
                    .putData("title", "Subscription")
                    .putData("body", "Welcome to Foresight")
                    .setToken(registrationToken)
                    .build();
            String response = fcm.send(msg);
            System.out.println("Successfully sent message: " + response);
        }
    }


    @DeleteMapping("/subscriptions/{userId}/{registrationToken}")
    public void deleteSubscription(@PathVariable("userId") String id, @PathVariable String registrationToken) throws FirebaseMessagingException {
       notificationService.unsubscribeToNotificationService(registrationToken,id);
    }
}


