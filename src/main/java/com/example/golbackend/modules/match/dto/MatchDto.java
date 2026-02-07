package com.example.golbackend.modules.match.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchDto {

    private Long championshipId;
    private Long phaseId;

    private Long homeTeamId;
    private Long awayTeamId;

    private LocalDateTime matchDate;
    private String fieldName;
    private String refereeName;

    private Integer roundNumber;
    private String groupIdentifier;
    private String bracketCode;
    private Integer leg;

    private Integer matchdayNumber;

    private Long winnerGoesToMatchId;
    private Long loserGoesToMatchId;
}
