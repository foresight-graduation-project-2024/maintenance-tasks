package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TaskCollection;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TaskSequence;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TeamCollection;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.User;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.CreateTeamRequest;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.TeamSearchCriteria;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.TeamSummary;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.ErrorCode;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.RuntimeErrorCodedException;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Member;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Notification;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.NotificationMessages;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Task;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.mapper.TeamMapper;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.TaskCollectionRepo;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.TaskSequenceRepo;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.TeamCollectionRepo;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Service
@AllArgsConstructor
public class TeamService {
   private TeamMapper mapper;
    private MongoTemplate template;
    private TeamCollectionRepo teamCollectionRepo;
    private UserService userService;
    private NotificationService notificationService;
    private TaskSequenceRepo taskSequenceRepo;
    private TaskCollectionRepo taskCollectionRepo;
    private UserRepo userRepo;
    public void createTeam(CreateTeamRequest request){
        TeamCollection team = teamCollectionRepo.save(mapper.createTeamRequestToTeamCollection(request));
        if(request.getMembers()!=null && !request.getMembers().isEmpty())
         userService.addOrUpdateUsersTeams(team);
        userService.addUser(team);
        taskSequenceRepo.save(new TaskSequence(team.getTeamId(), 1L));
        notificationService.pushTeamNotification(team.getTeamId(), Notification.builder()
                .notificationId(UUID.randomUUID().toString())
                .receiver(team.getTeamId())
                .content(NotificationMessages.TEAM_CREATED.getMessage("Technical Manager"))
                .issuedDate(new Date())
                .build());
    }

    public Page<TeamCollection> getAll(Pageable page) {
        return teamCollectionRepo.findAll(page);
    }

    public TeamCollection getTeam(String id) {
        return teamCollectionRepo.findById(id).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION));
    }

    public void deleteTeam(String teamId){
        TeamCollection teamCollection=teamCollectionRepo.findById(teamId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION));
        userService.deleteUserTeam(teamId);
        deleteTeamTasks(teamId);
        taskSequenceRepo.deleteById(teamId);
        teamCollectionRepo.deleteById(teamId);
//        notificationService.pushTeamNotification(teamId, Notification.builder()
//                .notificationId(UUID.randomUUID().toString())
//                .receiver(teamId)
//                .content(NotificationMessages.TEAM_DELETED.getMessage(teamCollection.getName()))
//                .issuedDate(new Date())
//                .build());
    }
    public void updateTeam(TeamCollection team){
        TeamCollection oldTeam= teamCollectionRepo.findById(team.getTeamId()).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION));
        if(team.getMembers()!=null) {
            userService.addOrUpdateUsersTeams(team);
            oldTeam.setMembers(team.getMembers());
        }
        oldTeam.setName(team.getName());
        oldTeam.setDescription(team.getDescription());
        oldTeam.setTeamLeader(team.getTeamLeader());
//        oldTeam.setSignature(team.getSignature());
        teamCollectionRepo.save(oldTeam);
//        notificationService.pushTeamNotification(team.getTeamId(), Notification.builder()
//                .notificationId(UUID.randomUUID().toString())
//                .receiver(team.getTeamId())
//                .content(NotificationMessages.TEAM_UPDATED.getMessage(team.getName()))
//                .issuedDate(new Date())
//                .build());
    }

    public Page<TeamSummary> getTeamsSummary(Pageable pageable) {

        List<TeamSummary> summaries= template.find(new Query().with(pageable),TeamSummary.class,"team");
        long count =template.count(new Query().with(pageable),TeamSummary.class,"team");
        return new PageImpl<>(summaries,pageable, count);

    }

    public Page<TeamSummary> searchSummaries(Pageable pageable, TeamSearchCriteria criteria) {
        Query q=new Query();
        if(criteria.getName()!=null && !criteria.getName().isEmpty())
            q.addCriteria(Criteria.where("name").is(criteria.getName()));
        if( (criteria.getLeaderFirstname()!=null && !criteria.getLeaderFirstname().isEmpty()))
            q.addCriteria(Criteria.where("teamLeader.firstname").is(criteria.getLeaderFirstname()));
        if((criteria.getLeaderLastname()!=null && !criteria.getLeaderLastname().isEmpty()) )
            q.addCriteria(Criteria.where("teamLeader.lastname").is(criteria.getLeaderLastname()));
        if(criteria.getLeaderRole()!=null && !criteria.getLeaderRole().isEmpty())
            q.addCriteria(Criteria.where("teamLeader.role").is(criteria.getLeaderRole()));
        q.with(pageable);
        List<TeamSummary> summaries= template.find(q,TeamSummary.class,"team");
        long count =template.count(q,TeamSummary.class,"team");
        return new PageImpl<>(summaries,pageable, count);
    }


    public void addTask(Task task1, String teamId) {
        TeamCollection team= teamCollectionRepo.findById(teamId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION));
        if(team.getTeamTasks()==null)
            team.setTeamTasks(new ArrayList<>());
        team.getTeamTasks().add(task1);
        teamCollectionRepo.save(team);
    }

    public void editTask(Task task, String teamId) {
        TeamCollection team= teamCollectionRepo.findById(teamId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION));
        List<Task> tasks= team.getTeamTasks().stream().map(t -> t.getTaskId().equals(task.getTaskId()) ? task : t).toList();
        team.setTeamTasks(tasks);
        teamCollectionRepo.save(team);
    }

    public void deleteTeamTask(String teamId, String taskId) {
        TeamCollection team= teamCollectionRepo.findById(teamId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION));
        team.getTeamTasks().removeIf(task -> Objects.equals(task.getTaskId(), taskId));
        teamCollectionRepo.save(team);
    }
    public void addTeamMember(List<Member> members, String teamId){
        TeamCollection team= teamCollectionRepo.findById(teamId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION));
        if(team.getMembers()==null)
            team.setMembers(new ArrayList<>());
        team.getMembers().addAll(members);
        teamCollectionRepo.save(team);
        userService.addOrUpdateUsersTeams(team);

    }
    public void deleteTeamMember(String memberId,String teamId){
        TeamCollection team= teamCollectionRepo.findById(teamId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION));
        team.getMembers().removeIf(member -> Objects.equals(member.getMemberId(), memberId));
        List<String>tasksIds=new ArrayList<>();
        for (Task t:team.getTeamTasks()){
            if(t.getAssignee().getMemberId().equals(memberId)){
                tasksIds.add(t.getTaskId());
                t.setAssignee(null);
            }
        }
        if(!tasksIds.isEmpty()) {
            List<TaskCollection> taskCollection = taskCollectionRepo.findAll();
            for (String id : tasksIds) {
                for (TaskCollection task : taskCollection) {
                    if (task.getTaskId().equals(id)) {
                        task.setAssignee(null);
                        break;
                    }
                }
            }
            taskCollectionRepo.saveAll(taskCollection);
            User user=userRepo.findById(memberId).orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
            List<Task> userTasks=user.getTasks();
            for (String id:tasksIds) {
                userTasks.removeIf(t -> t.getTaskId().equals(id));
            }
            user.setTasks(userTasks);
            userRepo.save(user);
        }

        teamCollectionRepo.save(team);
        userService.deleteUserTeam(teamId);
    }
    private void deleteTeamTasks(String teamId) {
        Query query = new Query(Criteria.where("teamId").is(teamId));
        template.remove(query, TaskCollection.class);
    }
}
