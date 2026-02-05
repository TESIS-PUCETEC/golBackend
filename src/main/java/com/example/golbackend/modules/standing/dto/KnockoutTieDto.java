package com.example.golbackend.modules.standing.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KnockoutTieDto {
    private String bracketCode;
    private Long homeTeamId;
    private String homeTeamName;
    private Long awayTeamId;
    private String awayTeamName;

    private LegResultDto leg1;
    private LegResultDto leg2;

    private Integer aggregateHome;
    private Integer aggregateAway;

    private Long advancesTeamId;
    private String advancesTeamName;
}
