package com.example.golbackend.modules.standing.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LegResultDto {
    private Long matchId;
    private Integer homeGoals;
    private Integer awayGoals;
    private Integer homePenalties;
    private Integer awayPenalties;
    private Boolean isFinal;
}
