
package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.mapper;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.TeamCollection;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.CreateTeamRequest;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.dto.UpdateTeamRequest;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeamMapper {
    TeamCollection createTeamRequestToTeamCollection(CreateTeamRequest request);
    TeamCollection updateTeamRequestToTeamCollection(UpdateTeamRequest request);
    Team teamCollectionToTeam(TeamCollection teamCollection);

}
