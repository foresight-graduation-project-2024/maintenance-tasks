package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TeamCollection;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.User;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Member;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Task;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Team;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.ErrorCode;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.RuntimeErrorCodedException;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.mapper.TeamMapper;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.UserRepo;
import com.mongodb.BasicDBObject;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepo userRepo;
    private MongoTemplate mongoTemplate;
    private TeamMapper teamMapper;
    @Transactional
    public void addOrUpdateUsersTeams(TeamCollection teamCollection){
        //expected: add team to user and update team for users
        //found: adding team to user but add a new copy of team instead of update the old one
        List<Member> members =teamCollection.getMembers();
        Team team= teamMapper.teamCollectionToTeam(teamCollection);
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, User.class);
        for (Member member: members) {
            Query query = new Query(where("userId").is(member.getMemberId()));
            Update addToSetUpdate = new Update().addToSet("teams", team); // Add the updated team to the array
            Update pullUpdate = new Update().pull("teams", new BasicDBObject("teamId", team.getTeamId())); // Remove the existing team from the array

            bulkOps.upsert(query, pullUpdate);
            bulkOps.upsert(query, addToSetUpdate);
        }
        bulkOps.execute();
    }

    public void deleteUsersCollectionTeam(String teamId) {
        Query query = new Query(where("teams.teamId").is(teamId));
        Update update = new Update().pull("teams", new BasicDBObject("teamId", teamId));
        mongoTemplate.updateMulti(query, update, User.class);
    }
    public void deleteUserTeam(String userId ,String teamId){
        Query query = new Query(where("userId").is(userId));
        Update pullUpdate = new Update().pull("teams", new BasicDBObject("teamId", teamId));
        mongoTemplate.upsert(query,pullUpdate,User.class);
    }

    public void addTask(Task task, String memberId) {
        User user =userRepo.findById(memberId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        if(user.getTasks()==null)
            user.setTasks(new ArrayList<>());
        user.getTasks().add(task);
        userRepo.save(user);
    }

    public void editTask(Task task) {
        User user=userRepo.findById(task.getAssignee().getMemberId()).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        if(user.getTasks()==null || user.getTasks().isEmpty()) {
            //throw new RuntimeErrorCodedException(ErrorCode.TASK_NOT_FOUND_EXCEPTION);
            user.setTasks(List.of(task));
            userRepo.save(user);
        }else {
            List<Task> userTasks = user.getTasks();
            boolean taskExists = userTasks.stream().anyMatch(t -> t.getTaskId().equals(task.getTaskId()));

            if (taskExists) {
                // Update existing task
                List<Task> updatedTasks = userTasks.stream()
                        .map(t -> t.getTaskId().equals(task.getTaskId()) ? task : t)
                        .toList();
                user.setTasks(updatedTasks);
            } else {
                // Add new task
                userTasks.add(task);
            }

            userRepo.save(user);
        }
    }

    public void deleteTask(String memberId, String taskId) {
        User user=userRepo.findById(memberId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        user.getTasks().removeIf(task -> Objects.equals(task.getTaskId(), taskId));
        userRepo.save(user);
    }

    public User getUser(String id) {
        return userRepo.findById(id).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
    }

    public Page<Team> getUserTeams(Pageable pageable, String userId) {
        //getting the total number of tasks
        ProjectionOperation projectTotal = project().and("teams").as("teams").andInclude("userId");
        TypedAggregation<User> aggregateTotal = newAggregation(User.class, match(where("userId").is(userId)), projectTotal);
        AggregationResults<User> totalTasksAggregation = mongoTemplate.aggregate(aggregateTotal, User.class, User.class);
        long count=0;
        List<Team> teams= new ArrayList<>();
        if(totalTasksAggregation.getMappedResults().size()!=0) {
            if (totalTasksAggregation.getMappedResults().get(0).getTeams() != null) {
                count = totalTasksAggregation.getMappedResults().get(0).getTeams().size();
                //getting a page of teams
                ProjectionOperation project = project().and("teams").slice(pageable.getPageSize(), (int) pageable.getOffset()).as("teams").andInclude("userId");
                TypedAggregation<User> agg = newAggregation(User.class, match(where("userId").is(userId)), project);
                AggregationResults<User> aggregate = mongoTemplate.aggregate(agg, User.class, User.class);
                teams = aggregate.getMappedResults().get(0).getTeams();
            }
        }else
            throw new RuntimeErrorCodedException(ErrorCode.USER_NOT_FOUND_EXCEPTION);
        return new PageImpl<>(teams,pageable,count);
    }

    public Page<Task> getUserTasks(Pageable pageable, String userId) {
        //getting the total number of tasks
        ProjectionOperation projectTotal = project().and("tasks").as("tasks").andInclude("userId");
        TypedAggregation<User> aggregateTotal = newAggregation(User.class, match(where("userId").is(userId)), projectTotal);
        AggregationResults<User> totalTasksAggregation = mongoTemplate.aggregate(aggregateTotal, User.class, User.class);
        long count=0;
        List<Task> tasks= new ArrayList<>();
        if(totalTasksAggregation.getMappedResults().size()!=0) {
            if (totalTasksAggregation.getMappedResults().get(0).getTasks() != null) {
                count = totalTasksAggregation.getMappedResults().get(0).getTasks().size();
                //getting a page of tasks
                ProjectionOperation project = project().and("tasks").slice(pageable.getPageSize(), (int) pageable.getOffset()).as("tasks").andInclude("userId");
                TypedAggregation<User> agg = newAggregation(User.class, match(where("userId").is(userId)), project);
                AggregationResults<User> aggregate = mongoTemplate.aggregate(agg, User.class, User.class);
                tasks = aggregate.getMappedResults().get(0).getTasks();
            }
        }else
            throw new RuntimeErrorCodedException(ErrorCode.USER_NOT_FOUND_EXCEPTION);
        return new PageImpl<>(tasks,pageable,count);
    }

    public void addOrUpdateTeamLeader(TeamCollection teamCollection) {
        Team team= teamMapper.teamCollectionToTeam(teamCollection);
        Query query = new Query(where("userId").is(teamCollection.getTeamLeader().getMemberId()));
        Update pullUpdate = new Update().pull("teams", new BasicDBObject("teamId", team.getTeamId())); // Remove the existing team from the array
        Update addToSetUpdate = new Update().addToSet("teams", team);
        mongoTemplate.upsert(query,pullUpdate,User.class);
        mongoTemplate.upsert(query,addToSetUpdate,User.class);
    }
}
