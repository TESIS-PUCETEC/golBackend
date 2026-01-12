package com.example.golbackend.modules.phase.model;

import com.example.golbackend.modules.phase.model.Phase;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private Phase.PhaseType type;

    @NotNull
    @Min(1)
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
