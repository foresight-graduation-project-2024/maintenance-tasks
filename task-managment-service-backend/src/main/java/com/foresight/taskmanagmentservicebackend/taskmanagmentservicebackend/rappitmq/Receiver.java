package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.rappitmq;



import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
//@RequiredArgsConstructor
//public class Receiver {
//
//  private final UserEventsHandler handler;
//
//
//
//
//
//    @RabbitListener(queues = Config.queueName)
//    public void receiveMessage(Map<String,Object> userCreatedData) {
//
//        System.out.println("Received <" + userCreatedData+ ">");
//
//        UserInfo userInfo = new UserInfo();
//
//        userInfo.setUserId(Long.valueOf((Integer)userCreatedData.get("id")));
//        userInfo.setFirstname((String) userCreatedData.get("firstname"));
//        userInfo.setLastname((String) userCreatedData.get("lastname"));
//        userInfo.setEmail((String) userCreatedData.get("email"));
//        userInfo.setRole((String) userCreatedData.get("role"));
//
//        handler.handelUserCreatedEvent(userInfo);
//
//
//
//
//    }
//
//
//
//
//}