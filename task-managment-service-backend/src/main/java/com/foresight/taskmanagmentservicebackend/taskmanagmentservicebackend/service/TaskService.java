package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.*;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.CreateCommentDto;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.CreateTaskRequest;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.TaskSearchCriteria;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.TaskSummary;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.ErrorCode;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.RuntimeErrorCodedException;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.*;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.mapper.TaskMapper;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.TaskCollectionRepo;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.mongodb.BasicDBObject;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@AllArgsConstructor
public class TaskService {
    private TaskMapper mapper;
    private TaskCollectionRepo taskCollectionRepo;
    private UserService userService;
    private TeamService teamService;
    private NotificationService notificationService;
    private MongoTemplate mongoTemplate;
    private UserInfoService userInfoService;
    @Transactional
    public void createTask(CreateTaskRequest taskRequest, String teamId) {
        TaskCollection taskCollection = mapper.createTaskRequestToTaskCollection( taskRequest);
        taskCollection.setTeamId(teamId);
        long nextSequence = getNextSequence(teamId);
        taskCollection.setTitle(getUniqueTaskTitle(teamId,nextSequence));
        taskCollection = taskCollectionRepo.save(taskCollection);
        Task task1 = mapper.taskCollectionToTask(taskCollection);
        teamService.addTask(task1, teamId);
        if(taskRequest.getAssignee()!=null)
         userService.addTask(task1, taskRequest.getAssignee().getMemberId());
        updateSequence(teamId, nextSequence + 1);
//        notificationService.pushUserNotification(taskRequest.getAssignee().getMemberId(), Notification.builder()
//                .notificationId(UUID.randomUUID().toString())
//                .receiver(taskRequest.getAssignee().getMemberId())
//              .content(NotificationMessages.TASK_CREATED.getMessage(taskRequest.getCreator().getFirstname()+" "+taskRequest.getCreator().getLastname()))
//                .issuedDate(new Date())
//                .build());
    }
    @Transactional
    public void editTask(TaskCollection taskCollection, String teamId)  {
        TaskCollection oldTask=taskCollectionRepo.findById(taskCollection.getTaskId()).orElseThrow(() -> new RuntimeErrorCodedException(ErrorCode.TASK_NOT_FOUND_EXCEPTION));
        //TaskCollection.builder().summary(taskCollection.getSummary()).assignee(taskCollection.getAssignee()).title(oldTask.getTitle()).priority(taskCollection.getPriority()).description(taskCollection.getDescription()).status(taskCollection.getStatus()).startDate(taskCollection.getStartDate()).endDate(taskCollection.getEndDate()).build();
        taskCollectionRepo.save(taskCollection);
        Task task = mapper.taskCollectionToTask(taskCollection);
        teamService.editTask(task, teamId);
        if(taskCollection.getAssignee()!=null) {
            userService.editTask(task);
//            notificationService.pushUserNotification(taskCollection.getAssignee().getMemberId(), Notification.builder()
//                    .notificationId(UUID.randomUUID().toString())
//                    .receiver(taskCollection.getAssignee().getMemberId())
//                    .content(NotificationMessages.TASK_UPDATED.getMessage(taskCollection.getTitle()))
//                    .issuedDate(new Date())
//                    .build());
            try {
                notificationService.userFCM(taskCollection.getAssignee().getMemberId(), Notification.builder()
                        .notificationId(UUID.randomUUID().toString())
                        .receiver(taskCollection.getAssignee().getMemberId())
                        .content(NotificationMessages.TASK_UPDATED.getMessage(taskCollection.getTitle()))
                        .issuedDate(new Date())
                        .build(),"task has been updated", "Task Update");
            } catch (FirebaseMessagingException e) {
                throw new RuntimeErrorCodedException(ErrorCode.FCM_ERROR);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public TaskCollection getTask(String id) {
        return taskCollectionRepo.findById(id).orElseThrow(() -> new RuntimeErrorCodedException(ErrorCode.TASK_NOT_FOUND_EXCEPTION));
    }

    public Page<TaskSummary> getSummaries(Pageable pageable) {
        List<TaskSummary> summaries = mongoTemplate.find(new Query().with(pageable), TaskSummary.class, "task");
        long count = mongoTemplate.count(new Query().with(pageable), TaskSummary.class, "task");
        return new PageImpl<>(summaries, pageable, count);
    }

    public Page<TaskSummary> searchSummaries(Pageable pageable, TaskSearchCriteria criteria) {
        Query query = new Query();
        if (criteria.getTitle() != null || !criteria.getTitle().isEmpty()) {
            query.addCriteria(Criteria.where("title").is(criteria.getTitle()));
        }
        if (criteria.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(criteria.getStatus()));
        }
        if (criteria.getPriority() != null) {
            query.addCriteria(Criteria.where("priority").is(criteria.getPriority()));
        }
        if (criteria.getStartDate() != null) {
            query.addCriteria(Criteria.where("startDate").is(criteria.getStartDate()));
        }
        if (criteria.getEndDate() != null) {
            query.addCriteria(Criteria.where("endDate").is(criteria.getEndDate()));
        }
        if( (criteria.getCreatorFirstname()!=null && !criteria.getCreatorFirstname().isEmpty()))
        {
            query.addCriteria(Criteria.where("creator.firstname").is(criteria.getCreatorFirstname()));
        }
        if( (criteria.getCreatorLastname()!=null && !criteria.getCreatorLastname().isEmpty()))
        {
            query.addCriteria(Criteria.where("creator.lastname").is(criteria.getCreatorLastname()));
        }

        if( (criteria.getAssigneeFirstname()!=null && !criteria.getAssigneeFirstname().isEmpty()))
        {
            query.addCriteria(Criteria.where("assignee.firstname").is(criteria.getCreatorFirstname()));
        }

        if( (criteria.getAssigneeLastname()!=null && !criteria.getAssigneeLastname().isEmpty()))
        {
            query.addCriteria(Criteria.where("assignee.lastname").is(criteria.getCreatorLastname()));
        }
        List<TaskSummary> summaries = mongoTemplate.find(query.with(pageable), TaskSummary.class, "task");
        long count = mongoTemplate.count(new Query().with(pageable), TaskSummary.class, "task");
        return new PageImpl<>(summaries, pageable, count);

    }
    @Transactional
    public boolean deleteTask(String teamId, String taskId) {
        TaskCollection taskCollection= taskCollectionRepo.findById(taskId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.TASK_NOT_FOUND_EXCEPTION));
        if(taskCollection.getAssignee()!=null)
            userService.deleteTask(taskCollection.getAssignee().getMemberId(),taskId);
        teamService.deleteTeamTask(teamId,taskId);
        taskCollectionRepo.deleteById(taskId);
        if(taskCollection.getAssignee()!=null) {
//        notificationService.pushUserNotification(taskCollection.getAssignee().getMemberId(), Notification.builder()
//                .notificationId(UUID.randomUUID().toString())
//                .receiver(taskCollection.getAssignee().getMemberId())
//                .content(NotificationMessages.TASK_DELETED.getMessage(taskCollection.getTitle()))
//                .issuedDate(new Date())
//                .build());
            try {
                notificationService.userFCM(taskCollection.getAssignee().getMemberId(), Notification.builder()
                        .notificationId(UUID.randomUUID().toString())
                        .receiver(taskCollection.getAssignee().getMemberId())
                        .content(NotificationMessages.TASK_DELETED.getMessage(taskCollection.getTitle()))
                        .issuedDate(new Date())
                        .build(),"task has been deleted","Task deleted");
            } catch (FirebaseMessagingException e) {
                throw new RuntimeErrorCodedException(ErrorCode.FCM_ERROR);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public void addComment(String taskId, CreateCommentDto commentDto)
    {
        Long id = commentDto.getOwnerId();
        UserInfo userInfo = userInfoService.getUserById(id);
        System.out.println(userInfo);

        Member owner = new Member(String.valueOf(userInfo.getUserId()), userInfo.getFirstname(), userInfo.getLastname(), userInfo.getEmail(), userInfo.getRole());
        Comment comment = new Comment(null,commentDto.getContent(),LocalDateTime.now(),owner,null);
        TaskCollection taskCollection = getTask(taskId);
        List<Comment> comments = taskCollection.getComments();
        if(comments==null)
        {
            comments = new ArrayList<>();
            taskCollection.setComments(comments);
        }
        comments.add(comment);
        editTask(taskCollection,taskCollection.getTeamId());



    }

    private long getNextSequence(String teamId) {
        Query query = new Query(Criteria.where("_id").is(teamId));
        Update update = new Update().inc("sequenceValue", 1);
        TaskSequence sequence = mongoTemplate.findAndModify(query, update, TaskSequence.class);
        return sequence != null ? sequence.getSequenceValue() : 1;
    }

    private void updateSequence(String teamId, long sequenceValue) {
        Query query = new Query(Criteria.where("_id").is(teamId));
        Update update = new Update().set("sequenceValue", sequenceValue);
        mongoTemplate.upsert(query, update, TaskSequence.class);
    }

    private String getUniqueTaskTitle(String teamId, long sequenceValue) {
        Query query = new Query(Criteria.where("_id").is(teamId));
        query.fields().include("signature");
        TeamCollection team = mongoTemplate.findOne(query, TeamCollection.class);
        String keyName = team != null ? team.getSignature() : null;

        return keyName + "-" + sequenceValue;
    }
}
