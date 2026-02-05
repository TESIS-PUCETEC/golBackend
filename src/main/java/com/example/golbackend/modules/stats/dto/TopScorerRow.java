package com.example.golbackend.modules.stats.dto;

public interface TopScorerRow {
    Long getPlayerId();
    String getPlayerName();
    Long getTeamId();
    String getTeamName();
    Long getGoals();
}
