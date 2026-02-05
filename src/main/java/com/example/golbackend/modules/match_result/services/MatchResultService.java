package com.example.golbackend.modules.match_result.services;

import com.example.golbackend.modules.match.model.Match;
import com.example.golbackend.modules.match.repositories.MatchRepository;
import com.example.golbackend.modules.match_event.model.MatchEventType;
import com.example.golbackend.modules.match_event.repositories.MatchEventRepository;
import com.example.golbackend.modules.match_result.model.MatchResult;
import com.example.golbackend.modules.match_result.repositories.MatchResultRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MatchResultService {

    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final MatchResultRepository matchResultRepository;

    public MatchResultService(
            MatchRepository matchRepository,
            MatchEventRepository matchEventRepository,
            MatchResultRepository matchResultRepository
    ) {
        this.matchRepository = matchRepository;
        this.matchEventRepository = matchEventRepository;
        this.matchResultRepository = matchResultRepository;
    }

    @Transactional
    public MatchResult recalculateFromEvents(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        Long homeTeamId = match.getHomeTeam().getTeamId();
        Long awayTeamId = match.getAwayTeam().getTeamId();

        Map<Long, Integer> goalsByTeam = toCountMap(
                matchEventRepository.countByTeamForEventType(matchId, MatchEventType.GOAL)
        );

        int homeGoals = goalsByTeam.getOrDefault(homeTeamId, 0);
        int awayGoals = goalsByTeam.getOrDefault(awayTeamId, 0);

        Map<Long, Integer> penByTeam = toCountMap(
                matchEventRepository.countScoredPenaltiesByTeam(matchId,MatchEventType.PENALTY_KICK)
        );

        Integer homePens = penByTeam.containsKey(homeTeamId) ? penByTeam.get(homeTeamId) : null;
        Integer awayPens = penByTeam.containsKey(awayTeamId) ? penByTeam.get(awayTeamId) : null;

        MatchResult result = matchResultRepository.findByMatchMatchId(matchId)
                .orElseGet(() -> {
                    MatchResult mr = new MatchResult();
                    mr.setMatch(match);
                    return mr;
                });

        result.setHomeGoals(homeGoals);
        result.setAwayGoals(awayGoals);

        boolean anyPenaltyEvents = (homePens != null || awayPens != null);
        result.setHomePenalties(anyPenaltyEvents ? (homePens == null ? 0 : homePens) : null);
        result.setAwayPenalties(anyPenaltyEvents ? (awayPens == null ? 0 : awayPens) : null);

        result.setRecalculatedAt(OffsetDateTime.now());

        return matchResultRepository.save(result);
    }

    @Transactional
    public MatchResult finalizeMatch(Long matchId) {
        MatchResult result = recalculateFromEvents(matchId);

        result.setIsFinal(true);
        result.setFinalizedAt(OffsetDateTime.now());
        return matchResultRepository.save(result);
    }

    private Map<Long, Integer> toCountMap(List<Object[]> rows) {
        Map<Long, Integer> map = new HashMap<>();
        for (Object[] r : rows) {
            Long teamId = ((Number) r[0]).longValue();
            Integer count = ((Number) r[1]).intValue();
            map.put(teamId, count);
        }
        return map;
    }

    @Transactional
    public MatchResult getOrCreateFromEvents(Long matchId) {
        return matchResultRepository.findByMatchMatchId(matchId)
                .orElseGet(() -> recalculateFromEvents(matchId));
    }
}
