package com.example.golbackend.modules.phase.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseResponse {

    private Long phaseId;
    private Long championshipId;

    private String name;
    private Phase.PhaseType type;
    private Integer phaseOrder;
    private Phase.PhaseStatus status;

    private Integer groupsCount;
    private Integer teamsPerGroup;
    private Integer qualifiersPerGroup;
    private Integer bestThirdQualifiers;
    private Boolean carryOverPoints;

    private Integer leagueQualifiersCount;

    private Integer knockoutStartCount;
    private Boolean hasThirdPlaceMatch;
    private Boolean awayGoalsRule;
    private Phase.PairingMethod pairingMethod;

    private Phase.MatchFormat matchFormat;
}
