package com.example.golbackend.modules.match_player.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchPlayerResponseDto {
    private Long matchPlayerId;
    private Long matchId;

    private Long playerId;
    private String playerFirstName;
    private String playerLastName;
    private String position;
    private Integer shirtNumber;

    private Long teamId;
    private String teamName;

    private boolean starting;
    private Integer minutesPlayed;
    private Integer goals;
    private Integer assists;
    private Integer yellowCards;
    private Integer redCards;
}
