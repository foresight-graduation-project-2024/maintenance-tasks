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
    public void addOrUpdateUsersTeams(TeamCollection teamCollection){
        List<Member> members =teamCollection.getMembers();
        Team team= teamMapper.teamCollectionToTeam(teamCollection);
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, User.class);
        for (Member member: members) {
            Query query = new Query(where("userId").is(member.getMemberId()));
            Update update = new Update().addToSet("teams", team);
            bulkOps.upsert(query, update);
        }
        bulkOps.execute();
    }

    public void deleteUserTeam(String teamId) {
        Query query = new Query(where("teams.teamId").is(teamId));
        Update update = new Update().pull("teams", new BasicDBObject("teamId", teamId));
        mongoTemplate.updateMulti(query, update, User.class);
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
        List<Task> userTasks=user.getTasks().stream()
                .map(t -> t.getTaskId().equals(task.getTaskId()) ? task : t)
                .toList();
        user.setTasks(userTasks);
        userRepo.save(user);
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
        long count=totalTasksAggregation.getMappedResults().get(0).getTeams().size();
        //getting a page of teams
        ProjectionOperation project = project().and("teams").slice(pageable.getPageSize(),(int) pageable.getOffset()).as("teams").andInclude("userId");
        TypedAggregation<User> agg = newAggregation(User.class, match(where("userId").is(userId)), project);
        AggregationResults<User> aggregate = mongoTemplate.aggregate(agg, User.class, User.class);
        List<Team> teams=aggregate.getMappedResults().get(0).getTeams();
        return new PageImpl<>(teams,pageable,count);
    }

    public Page<Task> getUserTasks(Pageable pageable, String userId) {
        //getting the total number of tasks
        ProjectionOperation projectTotal = project().and("tasks").as("tasks").andInclude("userId");
        TypedAggregation<User> aggregateTotal = newAggregation(User.class, match(where("userId").is(userId)), projectTotal);
        AggregationResults<User> totalTasksAggregation = mongoTemplate.aggregate(aggregateTotal, User.class, User.class);
        long count=totalTasksAggregation.getMappedResults().get(0).getTasks().size();

        //getting a page of tasks
        ProjectionOperation project = project().and("tasks").slice(pageable.getPageSize(),(int) pageable.getOffset()).as("tasks").andInclude("userId");
        TypedAggregation<User> agg = newAggregation(User.class, match(where("userId").is(userId)), project);
        AggregationResults<User> aggregate = mongoTemplate.aggregate(agg, User.class, User.class);
        List<Task> tasks=aggregate.getMappedResults().get(0).getTasks();
        return new PageImpl<>(tasks,pageable,count);
    }
}
