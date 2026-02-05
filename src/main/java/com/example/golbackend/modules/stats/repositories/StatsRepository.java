package com.example.golbackend.modules.stats.repositories;

import com.example.golbackend.modules.match_event.model.MatchEvent;
import com.example.golbackend.modules.match_event.model.MatchEventType;
import com.example.golbackend.modules.stats.dto.PlayerCardRow;
import com.example.golbackend.modules.stats.dto.TeamGoalsRow;
import com.example.golbackend.modules.stats.dto.TopScorerRow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatsRepository extends org.springframework.data.jpa.repository.JpaRepository<MatchEvent, Long>{

    @Query("""
        select
            e.player.playerId as playerId,
            concat(e.player.firstName, ' ', e.player.lastName) as playerName,
            e.team.id as teamId,
            e.team.name as teamName,
            count(e) as goals
        from MatchEvent e
        where e.match.phase.phaseId = :phaseId
          and e.eventType = :goalType
          and e.player is not null
          and e.team is not null
        group by e.player.playerId, e.player.firstName, e.player.lastName, e.team.id, e.team.name
        order by count(e) desc
    """)
    List<TopScorerRow> topScorers(@Param("phaseId") Long phaseId,
                                  @Param("goalType") MatchEventType goalType,
                                  Pageable pageable);

    @Query("""
        select
            e.player.playerId as playerId,
            concat(e.player.firstName, ' ', e.player.lastName) as playerName,
            e.team.id as teamId,
            e.team.name as teamName,
            count(e) as total
        from MatchEvent e
        where e.match.phase.phaseId = :phaseId
          and e.eventType = :cardType
          and e.player is not null
          and e.team is not null
        group by e.player.playerId, e.player.firstName, e.player.lastName, e.team.id, e.team.name
        order by count(e) desc
    """)
    List<PlayerCardRow> cardLeaders(@Param("phaseId") Long phaseId,
                                    @Param("cardType") MatchEventType cardType,
                                    Pageable pageable);

    @Query("""
        select
            e.team.id as teamId,
            e.team.name as teamName,
            count(e) as goals
        from MatchEvent e
        where e.match.phase.phaseId = :phaseId
          and e.eventType = :goalType
          and e.team is not null
        group by e.team.id, e.team.name
        order by count(e) desc
    """)
    List<TeamGoalsRow> teamGoals(@Param("phaseId") Long phaseId,
                                 @Param("goalType") MatchEventType goalType,
                                 Pageable pageable);
}
