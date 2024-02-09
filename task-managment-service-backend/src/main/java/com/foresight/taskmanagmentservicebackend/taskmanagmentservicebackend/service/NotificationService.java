package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TeamCollection;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserNotifications;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Member;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Notification;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.TeamCollectionRepo;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.UserNotificationRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@AllArgsConstructor
public class NotificationService {
    private UserNotificationRepo notificationRepo;
    private MongoTemplate mongoTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private TeamCollectionRepo teamCollectionRepo;

    public Page<Notification> getUserNotifications(Pageable pageable, String id){
        //getting the total number of notifications
        ProjectionOperation projectTotal = project().and("notifications").as("notifications").andInclude("userId");
        TypedAggregation<UserNotifications> aggregateTotal = newAggregation(UserNotifications.class, match(where("userId").is(id)), projectTotal);
        AggregationResults<UserNotifications> totalTasksAggregation = mongoTemplate.aggregate(aggregateTotal, UserNotifications.class, UserNotifications.class);
        long count=totalTasksAggregation.getMappedResults().get(0).getNotifications().size();

        //getting a page of notifications
        ProjectionOperation project = project().and("notifications").slice(pageable.getPageSize(),(int) pageable.getOffset()).as("notifications").andInclude("userId");
        TypedAggregation<UserNotifications> agg = newAggregation(UserNotifications.class, match(where("userId").is(id)), project);
        AggregationResults<UserNotifications> aggregate = mongoTemplate.aggregate(agg, UserNotifications.class, UserNotifications.class);
        List<Notification> notifications=aggregate.getMappedResults().get(0).getNotifications();
        return new PageImpl<>(notifications,pageable,count);
    }

    //for testing only
    public List<UserNotifications> getAllUsersNotifications(){
        return notificationRepo.findAll();
    }

    public void pushUserNotification(String userId, Notification notification){
        UserNotifications userNotifications =notificationRepo.findById(userId).orElse(new UserNotifications(userId,new ArrayList<>()));
        userNotifications.getNotifications().add(notification);
        notificationRepo.save(userNotifications);
        messagingTemplate.convertAndSendToUser(userId,"/topic/private-notifications", notification);
    }
    public void pushTeamNotification(String teamId, Notification notification){
        TeamCollection team= teamCollectionRepo.findById(teamId).orElse(null);
        List<UserNotifications> usersNotifications=new ArrayList<>();
        for (Member user:team.getMembers()) {
            UserNotifications userNotifications = notificationRepo.findById(user.getMemberId()).orElse(new UserNotifications(user.getMemberId(), new ArrayList<>()));
            userNotifications.getNotifications().add(notification);
            usersNotifications.add(userNotifications);

        }
        if(!usersNotifications.isEmpty())
         notificationRepo.saveAll(usersNotifications);
        for(Member user:team.getMembers())
         messagingTemplate.convertAndSendToUser(user.getMemberId(),"/topic/private-notifications", notification);
    }
}
