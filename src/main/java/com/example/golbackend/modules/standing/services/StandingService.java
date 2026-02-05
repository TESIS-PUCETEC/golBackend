package com.example.golbackend.modules.standing.services;

import com.example.golbackend.modules.match.model.Match;
import com.example.golbackend.modules.match.repositories.MatchRepository;
import com.example.golbackend.modules.match_result.model.MatchResult;
import com.example.golbackend.modules.match_result.repositories.MatchResultRepository;
import com.example.golbackend.modules.phase.model.Phase;
import com.example.golbackend.modules.phase.repositories.PhaseRepository;
import com.example.golbackend.modules.phase_team.repositories.PhaseTeamRepository;
import com.example.golbackend.modules.standing.model.Standing;
import com.example.golbackend.modules.standing.repositories.StandingRepository;
import com.example.golbackend.modules.team_managment.model.Team;
import com.example.golbackend.modules.team_managment.repositories.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StandingService {

    private final StandingRepository standingRepository;
    private final PhaseRepository phaseRepository;
    private final TeamRepository teamRepository;
    private final PhaseTeamRepository phaseTeamRepository;
    private final MatchRepository matchRepository;
    private final MatchResultRepository matchResultRepository;

    public StandingService(
            StandingRepository standingRepository,
            PhaseRepository phaseRepository,
            TeamRepository teamRepository,
            PhaseTeamRepository phaseTeamRepository,
            MatchRepository matchRepository,
            MatchResultRepository matchResultRepository
    ) {
        this.standingRepository = standingRepository;
        this.phaseRepository = phaseRepository;
        this.teamRepository = teamRepository;
        this.phaseTeamRepository = phaseTeamRepository;
        this.matchRepository = matchRepository;
        this.matchResultRepository = matchResultRepository;
    }

    public List<Standing> getStandings(Long phaseId) {
        return standingRepository.findByPhasePhaseIdOrderByPointsDescGoalDifferenceDescGoalsForDesc(phaseId);
    }

    @Transactional
    public List<Standing> recalculatePhase(Long phaseId) {
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found: " + phaseId));

        List<Long> teamIds = new ArrayList<>(new LinkedHashSet<>(
                phaseTeamRepository.findTeamIdsByPhaseId(phaseId)
        ));

        if (teamIds.isEmpty()) {
            standingRepository.deleteByPhasePhaseId(phaseId);
            standingRepository.flush();
            return List.of();
        }

        Map<Long, Standing> table = new HashMap<>();

        for (Long teamId : teamIds) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

            Standing s = Standing.builder()
                    .phase(phase)
                    .team(team)
                    .matchesPlayed(0).wins(0).draws(0).losses(0)
                    .goalsFor(0).goalsAgainst(0).goalDifference(0)
                    .points(0)
                    .build();

            table.put(teamId, s);
        }

        List<Match> matches = matchRepository.findByPhasePhaseId(phaseId);

        for (Match m : matches) {
            Optional<MatchResult> mrOpt = matchResultRepository.findByMatchMatchId(m.getMatchId());
            if (mrOpt.isEmpty()) continue;

            MatchResult mr = mrOpt.get();
            if (mr.getIsFinal() == null || !mr.getIsFinal()) continue;

            Long homeId = m.getHomeTeam().getId();
            Long awayId = m.getAwayTeam().getId();

            if (!table.containsKey(homeId) || !table.containsKey(awayId)) continue;

            int homeGoals = mr.getHomeGoals() == null ? 0 : mr.getHomeGoals();
            int awayGoals = mr.getAwayGoals() == null ? 0 : mr.getAwayGoals();

            Standing home = table.get(homeId);
            Standing away = table.get(awayId);

            home.setMatchesPlayed(home.getMatchesPlayed() + 1);
            away.setMatchesPlayed(away.getMatchesPlayed() + 1);

            home.setGoalsFor(home.getGoalsFor() + homeGoals);
            home.setGoalsAgainst(home.getGoalsAgainst() + awayGoals);

            away.setGoalsFor(away.getGoalsFor() + awayGoals);
            away.setGoalsAgainst(away.getGoalsAgainst() + homeGoals);

            if (homeGoals > awayGoals) {
                home.setWins(home.getWins() + 1);
                home.setPoints(home.getPoints() + 3);
                away.setLosses(away.getLosses() + 1);
            } else if (homeGoals < awayGoals) {
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

        for (Standing s : table.values()) {
            s.setGoalDifference(s.getGoalsFor() - s.getGoalsAgainst());
        }

        standingRepository.deleteByPhasePhaseId(phaseId);
        standingRepository.flush();

        List<Standing> saved = standingRepository.saveAll(table.values());

        saved.sort(Comparator
                .comparing(Standing::getPoints, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Standing::getGoalDifference, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Standing::getGoalsFor, Comparator.nullsLast(Comparator.reverseOrder()))
        );

        return saved;
    }

}
