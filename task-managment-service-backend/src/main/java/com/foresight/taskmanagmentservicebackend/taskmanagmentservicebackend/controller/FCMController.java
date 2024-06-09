package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.controller;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/fcm")
public class FCMController {
    private final FirebaseMessaging fcm;

    @PostMapping("/subscriptions/{topic}")
    public void createSubscription(@PathVariable("topic") String topic, @RequestBody List<String> registrationTokens) throws FirebaseMessagingException {
        fcm.subscribeToTopic(registrationTokens, topic);
        Message msg = Message.builder()
                .putData("notificationType", "TASK_UPDATE")
                .putData("title", "Subscription")
                .putData("body", "Welcome to Foresight")
                .setToken(registrationTokens.get(0))
                .build();
        fcm.send(msg);

    }

    @DeleteMapping("/subscriptions/{topic}/{registrationToken}")
    public void deleteSubscription(@PathVariable String topic, @PathVariable String registrationToken) throws FirebaseMessagingException {
        fcm.subscribeToTopic(Arrays.asList(registrationToken), topic);
    }
}
