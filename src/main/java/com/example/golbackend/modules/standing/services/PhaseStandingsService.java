package com.example.golbackend.modules.standing.services;

import com.example.golbackend.modules.match.model.Match;
import com.example.golbackend.modules.match.repositories.MatchRepository;
import com.example.golbackend.modules.match_result.model.MatchResult;
import com.example.golbackend.modules.match_result.repositories.MatchResultRepository;
import com.example.golbackend.modules.phase.model.Phase;
import com.example.golbackend.modules.phase.repositories.PhaseRepository;
import com.example.golbackend.modules.phase_team.model.PhaseTeam;
import com.example.golbackend.modules.phase_team.repositories.PhaseTeamRepository;
import com.example.golbackend.modules.standing.dto.*;
import com.example.golbackend.modules.team_managment.model.Team;
import com.example.golbackend.modules.team_managment.repositories.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhaseStandingsService {

    private final PhaseRepository phaseRepository;
    private final PhaseTeamRepository phaseTeamRepository;
    private final MatchRepository matchRepository;
    private final MatchResultRepository matchResultRepository;
    private final TeamRepository teamRepository;

    public PhaseStandingsService(
            PhaseRepository phaseRepository,
            PhaseTeamRepository phaseTeamRepository,
            MatchRepository matchRepository,
            MatchResultRepository matchResultRepository,
            TeamRepository teamRepository
    ) {
        this.phaseRepository = phaseRepository;
        this.phaseTeamRepository = phaseTeamRepository;
        this.matchRepository = matchRepository;
        this.matchResultRepository = matchResultRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Object getStandingsView(Long phaseId) {
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found: " + phaseId));

        return switch (phase.getType()) {
            case LEAGUE -> buildLeagueOrGroups(phase, false);
            case GROUP -> buildLeagueOrGroups(phase, true);
            case KNOCKOUT -> buildKnockout(phase);
        };
    }


    private PhaseStandingsResponseDto buildLeagueOrGroups(Phase phase, boolean isGroupPhase) {
        Long phaseId = phase.getPhaseId();

        List<PhaseTeam> pts = phaseTeamRepository.findByPhasePhaseId(phaseId);

        if (pts.isEmpty()) {
            return PhaseStandingsResponseDto.builder()
                    .phaseId(phaseId)
                    .phaseName(phase.getName())
                    .type(phase.getType().name())
                    .groups(List.of())
                    .build();
        }

        Map<String, List<PhaseTeam>> groups = isGroupPhase
                ? pts.stream().collect(Collectors.groupingBy(pt -> pt.getGroupIdentifier() == null ? "UNASSIGNED" : pt.getGroupIdentifier()))
                : Map.of("ALL", pts);

        List<Match> matches = matchRepository.findByPhasePhaseId(phaseId);

        List<GroupTableDto> groupTables = new ArrayList<>();

        for (Map.Entry<String, List<PhaseTeam>> entry : groups.entrySet()) {
            String groupKey = entry.getKey();
            List<PhaseTeam> groupTeams = entry.getValue();

            Map<Long, StandingRowDto> table = new HashMap<>();
            for (PhaseTeam pt : groupTeams) {
                Team t = pt.getTeam();
                int initialPts = pt.getInitialPoints() == null ? 0 : pt.getInitialPoints();

                table.put(t.getId(), StandingRowDto.builder()
                        .teamId(t.getId())
                        .teamName(t.getName())
                        .matchesPlayed(0).wins(0).draws(0).losses(0)
                        .goalsFor(0).goalsAgainst(0).goalDifference(0)
                        .points(initialPts)
                        .groupIdentifier(isGroupPhase ? groupKey : null)
                        .build());
            }

            for (Match m : matches) {
                if (m.getHomeTeam() == null || m.getAwayTeam() == null) continue;

                Long homeId = m.getHomeTeam().getId();
                Long awayId = m.getAwayTeam().getId();

                if (isGroupPhase) {
                    if (m.getGroupIdentifier() != null) {
                        if (!groupKey.equals(m.getGroupIdentifier())) continue;
                    } else {
                        if (!table.containsKey(homeId) || !table.containsKey(awayId)) continue;
                    }
                } else {
                    if (!table.containsKey(homeId) || !table.containsKey(awayId)) continue;
                }

                Optional<MatchResult> mrOpt = matchResultRepository.findByMatchMatchId(m.getMatchId());
                if (mrOpt.isEmpty()) continue;

                MatchResult mr = mrOpt.get();
                if (mr.getIsFinal() == null || !mr.getIsFinal()) continue;

                int hg = mr.getHomeGoals() == null ? 0 : mr.getHomeGoals();
                int ag = mr.getAwayGoals() == null ? 0 : mr.getAwayGoals();

                StandingRowDto home = table.get(homeId);
                StandingRowDto away = table.get(awayId);

                home.setMatchesPlayed(home.getMatchesPlayed() + 1);
                away.setMatchesPlayed(away.getMatchesPlayed() + 1);

                home.setGoalsFor(home.getGoalsFor() + hg);
                home.setGoalsAgainst(home.getGoalsAgainst() + ag);

                away.setGoalsFor(away.getGoalsFor() + ag);
                away.setGoalsAgainst(away.getGoalsAgainst() + hg);

                if (hg > ag) {
                    home.setWins(home.getWins() + 1);
                    home.setPoints(home.getPoints() + 3);
                    away.setLosses(away.getLosses() + 1);
                } else if (hg < ag) {
                    away.setWins(away.getWins() + 1);
                    away.setPoints(away.getPoints() + 3);
                    home.setLosses(home.getLosses() + 1);
                } else {
                    home.setDraws(home.getDraws() + 1);
                    away.setDraws(away.getDraws() + 1);
                    home.setPoints(home.getPoints() + 1);
                    away.setPoints(away.getPoints() + 1);
                }
            }

            List<StandingRowDto> ordered = new ArrayList<>(table.values());
            for (StandingRowDto r : ordered) {
                r.setGoalDifference(r.getGoalsFor() - r.getGoalsAgainst());
            }

            ordered.sort(Comparator
                    .comparing(StandingRowDto::getPoints, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(StandingRowDto::getGoalDifference, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(StandingRowDto::getGoalsFor, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(StandingRowDto::getTeamName, Comparator.nullsLast(String::compareToIgnoreCase))
            );

            groupTables.add(GroupTableDto.builder()
                    .groupIdentifier(groupKey)
                    .table(ordered)
                    .build());
        }

        groupTables.sort(Comparator.comparing(GroupTableDto::getGroupIdentifier));

        return PhaseStandingsResponseDto.builder()
                .phaseId(phaseId)
                .phaseName(phase.getName())
                .type(phase.getType().name())
                .groups(groupTables)
                .build();
    }


    private KnockoutBracketResponseDto buildKnockout(Phase phase) {
        Long phaseId = phase.getPhaseId();
        List<Match> matches = matchRepository.findByPhasePhaseId(phaseId);

        Map<Integer, Map<String, List<Match>>> byRound = matches.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getRoundNumber() == null ? 0 : m.getRoundNumber(),
                        Collectors.groupingBy(m -> m.getBracketCode() == null ? "UNSPECIFIED" : m.getBracketCode())
                ));

        List<KnockoutRoundDto> rounds = new ArrayList<>();

        List<Integer> roundKeys = new ArrayList<>(byRound.keySet());
        roundKeys.sort(Integer::compareTo);

        for (Integer roundNumber : roundKeys) {
            Map<String, List<Match>> tiesMap = byRound.get(roundNumber);

            List<KnockoutTieDto> ties = new ArrayList<>();

            for (Map.Entry<String, List<Match>> e : tiesMap.entrySet()) {
                String bracketCode = e.getKey();
                List<Match> tieMatches = e.getValue();

                tieMatches.sort(Comparator.comparing(m -> m.getLeg() == null ? 1 : m.getLeg()));

                Match leg1Match = tieMatches.get(0);
                Match leg2Match = tieMatches.size() > 1 ? tieMatches.get(1) : null;

                LegResultDto leg1 = buildLegResult(leg1Match);
                LegResultDto leg2 = (phase.getMatchFormat() == Phase.MatchFormat.HOME_AWAY) ? buildLegResult(leg2Match) : null;

                Team h = leg1Match.getHomeTeam();
                Team a = leg1Match.getAwayTeam();

                Integer aggHome = 0;
                Integer aggAway = 0;

                if (leg1 != null && Boolean.TRUE.equals(leg1.getIsFinal())) {
                    aggHome += safe(leg1.getHomeGoals());
                    aggAway += safe(leg1.getAwayGoals());
                }
                if (leg2 != null && Boolean.TRUE.equals(leg2.getIsFinal())) {

                    aggHome += safe(leg2.getHomeGoals());
                    aggAway += safe(leg2.getAwayGoals());
                }

                Long advancesId = null;
                String advancesName = null;

                boolean tieComplete = Boolean.TRUE.equals(leg1.getIsFinal())
                        && (phase.getMatchFormat() == Phase.MatchFormat.SINGLE_MATCH || (leg2 != null && Boolean.TRUE.equals(leg2.getIsFinal())));

                if (tieComplete) {
                    if (phase.getMatchFormat() == Phase.MatchFormat.SINGLE_MATCH) {
                        advancesId = decideSingleMatchWinner(leg1Match, leg1);
                    } else {
                        advancesId = decideTwoLegWinner(phase, leg1Match, leg2Match, leg1, leg2);
                    }

                    if (advancesId != null) {
                        Team t = teamRepository.findById(advancesId).orElse(null);
                        if (t != null) advancesName = t.getName();
                    }
                }

                ties.add(KnockoutTieDto.builder()
                        .bracketCode(bracketCode)
                        .homeTeamId(h != null ? h.getId() : null)
                        .homeTeamName(h != null ? h.getName() : null)
                        .awayTeamId(a != null ? a.getId() : null)
                        .awayTeamName(a != null ? a.getName() : null)
                        .leg1(leg1)
                        .leg2(leg2)
                        .aggregateHome(aggHome)
                        .aggregateAway(aggAway)
                        .advancesTeamId(advancesId)
                        .advancesTeamName(advancesName)
                        .build());
            }

            ties.sort(Comparator.comparing(KnockoutTieDto::getBracketCode, Comparator.nullsLast(String::compareTo)));

            rounds.add(KnockoutRoundDto.builder()
                    .roundNumber(roundNumber)
                    .ties(ties)
                    .build());
        }

        return KnockoutBracketResponseDto.builder()
                .phaseId(phaseId)
                .phaseName(phase.getName())
                .type(phase.getType().name())
                .matchFormat(phase.getMatchFormat() != null ? phase.getMatchFormat().name() : null)
                .rounds(rounds)
                .build();
    }

    private LegResultDto buildLegResult(Match m) {
        if (m == null) return null;

        Optional<MatchResult> mrOpt = matchResultRepository.findByMatchMatchId(m.getMatchId());
        if (mrOpt.isEmpty()) {
            return LegResultDto.builder()
                    .matchId(m.getMatchId())
                    .homeGoals(null).awayGoals(null)
                    .homePenalties(null).awayPenalties(null)
                    .isFinal(false)
                    .build();
        }

        MatchResult mr = mrOpt.get();
        return LegResultDto.builder()
                .matchId(m.getMatchId())
                .homeGoals(mr.getHomeGoals())
                .awayGoals(mr.getAwayGoals())
                .homePenalties(mr.getHomePenalties())
                .awayPenalties(mr.getAwayPenalties())
                .isFinal(Boolean.TRUE.equals(mr.getIsFinal()))
                .build();
    }

    private Long decideSingleMatchWinner(Match match, LegResultDto leg) {
        int hg = safe(leg.getHomeGoals());
        int ag = safe(leg.getAwayGoals());

        if (hg > ag) return match.getHomeTeam().getId();
        if (ag > hg) return match.getAwayTeam().getId();

        Integer hp = leg.getHomePenalties();
        Integer ap = leg.getAwayPenalties();
        if (hp != null && ap != null) {
            if (hp > ap) return match.getHomeTeam().getId();
            if (ap > hp) return match.getAwayTeam().getId();
        }

        return null;
    }

    private Long decideTwoLegWinner(Phase phase, Match leg1Match, Match leg2Match, LegResultDto leg1, LegResultDto leg2) {

        int total1 = safe(leg1.getHomeGoals()) + safe(leg2.getAwayGoals());
        int total2 = safe(leg1.getAwayGoals()) + safe(leg2.getHomeGoals());

        if (total1 > total2) return leg1Match.getHomeTeam().getId();
        if (total2 > total1) return leg1Match.getAwayTeam().getId();

        if (Boolean.TRUE.equals(phase.getAwayGoalsRule())) {
            int awayGoalsTeam1 = safe(leg2.getAwayGoals());
            int awayGoalsTeam2 = safe(leg1.getAwayGoals());
            if (awayGoalsTeam1 > awayGoalsTeam2) return leg1Match.getHomeTeam().getId();
            if (awayGoalsTeam2 > awayGoalsTeam1) return leg1Match.getAwayTeam().getId();
        }

        if (leg2.getHomePenalties() != null && leg2.getAwayPenalties() != null) {
            if (leg2.getHomePenalties() > leg2.getAwayPenalties()) return leg2Match.getHomeTeam().getId();
            if (leg2.getAwayPenalties() > leg2.getHomePenalties()) return leg2Match.getAwayTeam().getId();
        }

        return null;
    }

    private int safe(Integer v) { return v == null ? 0 : v; }
}
