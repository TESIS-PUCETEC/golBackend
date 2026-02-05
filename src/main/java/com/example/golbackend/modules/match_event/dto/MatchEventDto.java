package com.example.golbackend.modules.match_event.dto;

import com.example.golbackend.modules.match_event.model.MatchEventType;
import com.example.golbackend.modules.match_event.model.MatchPeriodType;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MatchEventDto {
    private MatchEventType eventType;
    private MatchPeriodType period;

    private OffsetDateTime occurredAt;

    private Integer clockMinute;
    private Integer clockSecond;
    private Integer clockSeconds;

    private Long playerId;
    private Long relatedPlayerId;

    private Long teamId;

    private Integer shootoutKickOrder;
    private Integer shootoutRound;

    private Boolean penaltyScored;

    private String notes;
}
