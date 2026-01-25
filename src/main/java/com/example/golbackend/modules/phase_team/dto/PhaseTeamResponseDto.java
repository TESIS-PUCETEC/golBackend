package com.example.golbackend.modules.phase_team.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhaseTeamResponseDto {
    private Long phaseTeamId;
    private Long phaseId;

    private Long teamId;
    private String teamName;

    private String groupIdentifier;
    private Integer initialPoints;
    private Integer seed;
    private Integer qualifiedPosition;
}
