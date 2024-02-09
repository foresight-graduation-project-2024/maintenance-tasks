package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "UserNotifications")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserNotifications {
    @Id
    private String userId;
    private List<Notification> notifications;
}
