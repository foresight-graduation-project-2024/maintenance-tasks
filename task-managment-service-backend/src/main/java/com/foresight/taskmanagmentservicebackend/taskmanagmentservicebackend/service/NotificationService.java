package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TeamCollection;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserNotifications;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserRegistrationToken;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.ErrorCode;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.RuntimeErrorCodedException;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Member;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Notification;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.TeamCollectionRepo;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.UserNotificationRepo;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.UserRegistrationTokenRepo;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final FirebaseMessaging fcm;
    private final UserRegistrationTokenRepo registrationTokenRepo;

    public Page<Notification> getUserNotifications(Pageable pageable, String id){
        //Sort sort = Sort.by(Sort.Direction.DESC, "issuedDate");
        //getting the total number of notifications
        ProjectionOperation projectTotal = project().and("notifications").as("notifications").andInclude("userId");
        TypedAggregation<UserNotifications> aggregateTotal = newAggregation(UserNotifications.class, match(where("userId").is(id)), projectTotal);
        AggregationResults<UserNotifications> totalTasksAggregation = mongoTemplate.aggregate(aggregateTotal, UserNotifications.class, UserNotifications.class);
        long count=0;
        if(totalTasksAggregation.getMappedResults().size() >0)
         count=totalTasksAggregation.getMappedResults().get(0).getNotifications().size();
        List<Notification> notifications=new ArrayList<>();
        //getting a page of notifications
        if(count >0) {
            ProjectionOperation project = project().and("notifications").slice(pageable.getPageSize(), (int) pageable.getOffset()).as("notifications").andInclude("userId");
            TypedAggregation<UserNotifications> agg = newAggregation(UserNotifications.class, match(where("userId").is(id)), project);
            AggregationResults<UserNotifications> aggregate = mongoTemplate.aggregate(agg, UserNotifications.class, UserNotifications.class);
            notifications = aggregate.getMappedResults().get(0).getNotifications();
        }
        return new PageImpl<>(notifications,pageable,count);
    }

    //for testing only
    public List<UserNotifications> getAllUsersNotifications(){
        return notificationRepo.findAll();
    }

//    public void pushUserNotification(String userId, Notification notification){
//        UserNotifications userNotifications =notificationRepo.findById(userId).orElse(new UserNotifications(userId,new ArrayList<>()));
//        userNotifications.getNotifications().add(notification);
//        notificationRepo.save(userNotifications);
//        messagingTemplate.convertAndSendToUser(userId,"/topic/private-notifications", notification);
//    }
    @Transactional
    public void userFCM(String userId , Notification notification,String title , String type) throws FirebaseMessagingException, IOException {
        UserNotifications userNotifications =notificationRepo.findById(userId).orElse(new UserNotifications(userId,new ArrayList<>()));
        userNotifications.getNotifications().add(notification);
        notificationRepo.save(userNotifications);
//        notificationRepo.save(userNotifications);
//        Message msg = Message.builder()
//                .setTopic("user-"+userId)
//                .putData("content", notification.getContent())
//                .build();
//       String response= fcm.send(msg);
//       System.out.println("response"+response);
        UserRegistrationToken userRegistrationTokens=registrationTokenRepo.findById(userId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.MEMBER_NOT_FOUND));
        sendExpoNotification(userRegistrationTokens.getTokens(),title,notification.getContent(),type);
    }


    public void sendExpoNotification(List<String> expoTokens, String title, String body, String type) throws IOException {
        String imageUrl = "http://localhost:8085/foresight.jpg"; // Local URL to the image

        String url = "https://exp.host/--/api/v2/push/send";

        for (String expoToken : expoTokens) {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = String.format("{\"to\": \"%s\", \"title\": \"%s\", \"body\": \"%s\", \"data\": {\"notificationType\": \"%s\", \"icon\": \"%s\"}}", expoToken, title, body, type, imageUrl);
            System.out.println("payload: " + payload);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Expo notification response code: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Failed to send notification: " + responseCode);
            }

            conn.disconnect();
        }
    }

    @Transactional
    public void subscribeToNotificationService(String token, String id) {
        if (token == null || id == null) {
            throw new RuntimeErrorCodedException(ErrorCode.FCM_ERROR);
        }
        UserRegistrationToken userRegistrationTokens=registrationTokenRepo.findById(id).orElse(new UserRegistrationToken());
        if(userRegistrationTokens.getUserId()==null)
            userRegistrationTokens.setUserId(id);
        // Ensure the tokens list is initialized
        if (userRegistrationTokens.getTokens() == null) {
            userRegistrationTokens.setTokens(new ArrayList<>());
        }
        // Add the token if it is not already present
        if (!userRegistrationTokens.getTokens().contains(token)) {
            userRegistrationTokens.getTokens().add(token);
        }
        registrationTokenRepo.save(userRegistrationTokens);
    }
    @Transactional
    public void unsubscribeToNotificationService(String token , String id) {
        UserRegistrationToken userRegistrationTokens=registrationTokenRepo.findById(id).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.MEMBER_NOT_FOUND));
        if (userRegistrationTokens.getTokens() != null && userRegistrationTokens.getTokens().contains(token)) {
            userRegistrationTokens.getTokens().remove(token);
        }
        registrationTokenRepo.save(userRegistrationTokens);
    }
    public void pushTeamNotification(String teamId, Notification notification) {
        Optional<TeamCollection> team = teamCollectionRepo.findById(teamId);
        List<UserNotifications> usersNotifications = new ArrayList<>();
        if (team.isPresent() && team.get().getMembers()!=null) {
            for (Member user : team.get().getMembers()) {
                UserNotifications userNotifications = notificationRepo.findById(user.getMemberId()).orElse(new UserNotifications(user.getMemberId(), new ArrayList<>()));
                userNotifications.getNotifications().add(notification);
                usersNotifications.add(userNotifications);

            }
            if (!usersNotifications.isEmpty())
                notificationRepo.saveAll(usersNotifications);
            for (Member user : team.get().getMembers())
                messagingTemplate.convertAndSendToUser(user.getMemberId(), "/topic/private-notifications", notification);
        }
    }
//    @Transactional
//    public void teamFCMNotification(String teamId, Notification notification,String title,String type) throws IOException, FirebaseMessagingException {
//        Optional<TeamCollection> team = teamCollectionRepo.findById(teamId);
//        List<UserNotifications> usersNotifications = new ArrayList<>();
//        if (team.isPresent() && team.get().getMembers()!=null) {
//            for (Member user : team.get().getMembers()) {
//                userFCM(user.getMemberId(), notification,title,type);
//
//            }
//
//        }
//    }
    public void markNotificationsAsSeen(String userId) {
        Optional<UserNotifications> optionalUserNotifications = notificationRepo.findById(userId);
        optionalUserNotifications.ifPresent(userNotifications -> {
            List<Notification> notifications = userNotifications.getNotifications();
            if (notifications != null) {
                for (Notification notification : notifications) {
                        notification.setSeen(true);
                }
                notificationRepo.save(userNotifications);
            }
        });
    }
}
