package com.example.golbackend.modules.standing.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StandingRowDto {
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

    private String groupIdentifier;
}
