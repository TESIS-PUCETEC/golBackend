package com.example.golbackend.modules.match_result.dto;

import com.example.golbackend.modules.match_result.model.MatchResult;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class MatchResultResponseDto {

    private Long matchId;
    private Integer homeGoals;
    private Integer awayGoals;
    private Integer homePenalties;
    private Integer awayPenalties;
    private Boolean isFinal;
    private OffsetDateTime finalizedAt;
    private OffsetDateTime recalculatedAt;

    public static MatchResultResponseDto from(MatchResult mr) {
        MatchResultResponseDto dto = new MatchResultResponseDto();
        dto.matchId = mr.getMatch().getMatchId();
        dto.homeGoals = mr.getHomeGoals();
        dto.awayGoals = mr.getAwayGoals();
        dto.homePenalties = mr.getHomePenalties();
        dto.awayPenalties = mr.getAwayPenalties();
        dto.isFinal = mr.getIsFinal();
        dto.finalizedAt = mr.getFinalizedAt();
        dto.recalculatedAt = mr.getRecalculatedAt();
        return dto;
    }

}
