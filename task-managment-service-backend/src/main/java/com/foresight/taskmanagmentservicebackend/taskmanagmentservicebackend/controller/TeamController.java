package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.controller;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.TeamSearchCriteria;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.TeamSummary;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Member;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service.TeamService;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TeamCollection;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.CreateTeamRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/team")
@AllArgsConstructor
@CrossOrigin

public class TeamController {
    private TeamService teamService;
    @PostMapping
    public void addTeam(@RequestBody @Valid CreateTeamRequest createTeamRequest){
            teamService.createTeam(createTeamRequest);
    }
    @GetMapping
    public Page<TeamCollection> getAllTeams(Pageable pageable){
        return teamService.getAll(pageable);
    }
    @GetMapping("/{id}")
    public TeamCollection getTeam(@PathVariable String id){
        return teamService.getTeam(id);
    }
    @PutMapping()
    public void updateTeam(@RequestBody TeamCollection teamCollection){
        teamService.updateTeam(teamCollection);
    }
    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable String id){
        teamService.deleteTeam(id);
    }
    @GetMapping("/summaries")
    public Page<TeamSummary> getTeamsSummary(Pageable pageable){
        return teamService.getTeamsSummary(pageable);
    }
    @GetMapping("/summaries/search")
    public Page<TeamSummary> searchTeamsSummary(Pageable pageable, TeamSearchCriteria criteria){
        return teamService.searchSummaries(pageable,criteria);
    }
    @PostMapping("/member/{teamId}")
    public void addTeamMember(@RequestBody List<Member> members, @PathVariable("teamId") String id){
        teamService.addTeamMember(members,id);
    }
    @DeleteMapping("/member/{teamId}/{memberId}")
    public void deleteTeamMember(@PathVariable String memberId,@PathVariable String teamId){
        teamService.deleteTeamMember(memberId,teamId);
    }
}
