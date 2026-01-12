package com.example.golbackend.modules.phase.model;

import com.example.golbackend.modules.championship_managment.model.Championship;

public class PhaseMapper {

    private PhaseMapper() {}

    public static Phase toEntity(PhaseCreateRequest req, Championship championship) {
        return Phase.builder()
                .championship(championship)
                .name(req.getName())
                .type(req.getType())
                .phaseOrder(req.getPhaseOrder())
                .status(req.getStatus())
                .groupsCount(req.getGroupsCount())
                .teamsPerGroup(req.getTeamsPerGroup())
                .qualifiersPerGroup(req.getQualifiersPerGroup())
                .bestThirdQualifiers(req.getBestThirdQualifiers())
                .carryOverPoints(req.getCarryOverPoints())
                .leagueQualifiersCount(req.getLeagueQualifiersCount())
                .knockoutStartCount(req.getKnockoutStartCount())
                .hasThirdPlaceMatch(req.getHasThirdPlaceMatch())
                .awayGoalsRule(req.getAwayGoalsRule())
                .pairingMethod(req.getPairingMethod())
                .matchFormat(req.getMatchFormat())
                .build();
    }

    public static PhaseResponse toResponse(Phase p) {
        return PhaseResponse.builder()
                .phaseId(p.getPhaseId())
                .championshipId(p.getChampionship() != null ? p.getChampionship().getChampionshipId() : null)
                .name(p.getName())
                .type(p.getType())
                .phaseOrder(p.getPhaseOrder())
                .status(p.getStatus())

                .groupsCount(p.getGroupsCount())
                .teamsPerGroup(p.getTeamsPerGroup())
                .qualifiersPerGroup(p.getQualifiersPerGroup())
                .bestThirdQualifiers(p.getBestThirdQualifiers())
                .carryOverPoints(p.getCarryOverPoints())

                .leagueQualifiersCount(p.getLeagueQualifiersCount())

                .knockoutStartCount(p.getKnockoutStartCount())
                .hasThirdPlaceMatch(p.getHasThirdPlaceMatch())
                .awayGoalsRule(p.getAwayGoalsRule())
                .pairingMethod(p.getPairingMethod())
                .matchFormat(p.getMatchFormat())
                .build();
    }
}
