package com.example.golbackend.modules.stats.services;

import com.example.golbackend.modules.match_event.model.MatchEventType;
import com.example.golbackend.modules.stats.dto.PlayerCardRow;
import com.example.golbackend.modules.stats.dto.TeamGoalsRow;
import com.example.golbackend.modules.stats.dto.TopScorerRow;
import com.example.golbackend.modules.stats.repositories.StatsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsService {

    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public List<TopScorerRow> getTopScorers(Long phaseId, int limit) {
        return statsRepository.topScorers(
                phaseId,
                MatchEventType.GOAL,
                PageRequest.of(0, Math.max(limit, 1))
        );
    }

    public List<PlayerCardRow> getYellowCards(Long phaseId, int limit) {
        return statsRepository.cardLeaders(
                phaseId,
                MatchEventType.YELLOW_CARD,
                PageRequest.of(0, Math.max(limit, 1))
        );
    }

    public List<PlayerCardRow> getRedCards(Long phaseId, int limit) {
        return statsRepository.cardLeaders(
                phaseId,
                MatchEventType.RED_CARD,
                PageRequest.of(0, Math.max(limit, 1))
        );
    }

    public List<TeamGoalsRow> getBestAttack(Long phaseId, int limit) {
        return statsRepository.teamGoals(
                phaseId,
                MatchEventType.GOAL,
                PageRequest.of(0, Math.max(limit, 1))
        );
    }
}
