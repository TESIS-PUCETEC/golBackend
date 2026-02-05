package com.example.golbackend.modules.stats.dto;

public interface PlayerCardRow {
    Long getPlayerId();
    String getPlayerName();
    Long getTeamId();
    String getTeamName();
    Long getTotal();
}
