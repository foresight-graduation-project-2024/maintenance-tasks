package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.rappitmq;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserInfo;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

//@Service
//@RequiredArgsConstructor
//public class UserEventsHandler
//{
//
//    private final RabbitTemplate rabbitTemplate;
//    private final UserInfoService userInfoService;
//
//    public void handelUserCreatedEvent(UserInfo userInfo)
//    {
//        userInfoService.addUser(userInfo);
//
//    }
//}
