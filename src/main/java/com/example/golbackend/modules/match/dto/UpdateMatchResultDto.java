package com.example.golbackend.modules.match.dto;

import lombok.Data;

@Data
public class UpdateMatchResultDto {
    private Integer homeScore;
    private Integer awayScore;

    private Integer homePenalties;
    private Integer awayPenalties;

    private String status;
}
