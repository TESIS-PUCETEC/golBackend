package com.example.golbackend.modules.standing.dto;

import com.example.golbackend.modules.standing.model.Standing;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StandingResponseDto {
    private Long standingId;
    private Long phaseId;
    private Long teamId;
    private String teamName;

    private Integer matchesPlayed;
    private Integer wins;
    private Integer draws;
    private Integer losses;
    private Integer goalsFor;
    private Integer goalsAgainst;
    private Integer goalDifference;
    private Integer points;

    public static StandingResponseDto from(Standing s) {
        StandingResponseDto dto = new StandingResponseDto();
        dto.standingId = s.getStandingId();
        dto.phaseId = s.getPhase().getPhaseId();
        dto.teamId = s.getTeam().getId();
        dto.teamName = s.getTeam().getName();

        dto.matchesPlayed = s.getMatchesPlayed();
        dto.wins = s.getWins();
        dto.draws = s.getDraws();
        dto.losses = s.getLosses();
        dto.goalsFor = s.getGoalsFor();
        dto.goalsAgainst = s.getGoalsAgainst();
        dto.goalDifference = s.getGoalDifference();
        dto.points = s.getPoints();
        return dto;
    }

}
