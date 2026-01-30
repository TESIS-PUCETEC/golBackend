package com.example.golbackend.modules.match.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MatchResponseDto {
    private Long matchId;

    private Long championshipId;
    private Long phaseId;

    private Long homeTeamId;
    private Long awayTeamId;

    private Integer homeScore;
    private Integer awayScore;

    private Integer homePenalties;
    private Integer awayPenalties;

    private String status;

    private LocalDateTime matchDate;
    private String fieldName;
    private String refereeName;

    private Integer roundNumber;
    private String groupIdentifier;
    private String bracketCode;
    private Integer leg;

    private Long winnerGoesToMatchId;
    private Long loserGoesToMatchId;
}
