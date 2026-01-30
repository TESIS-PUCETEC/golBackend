package com.example.golbackend.modules.match_event.repositories;

import com.example.golbackend.modules.match_event.model.MatchEvent;
import com.example.golbackend.modules.match_event.model.MatchEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {

    List<MatchEvent> findByMatchMatchIdOrderByPeriodAscClockSecondsAsc(Long matchId);

    @Query("""
        select e.team.id, count(e)
        from MatchEvent e
        where e.match.matchId = :matchId
          and e.eventType = :eventType
          and e.team is not null
        group by e.team.id
    """)
    List<Object[]> countByTeamForEventType(
            @Param("matchId") Long matchId,
            @Param("eventType") MatchEventType eventType
    );

    @Query("""
        select e.team.id, count(e)
        from MatchEvent e
        where e.match.matchId = :matchId
          and e.eventType = :eventType
          and e.penaltyScored = true
          and e.team is not null
        group by e.team.id
    """)
    List<Object[]> countScoredPenaltiesByTeam(
            @Param("matchId") Long matchId,
            @Param("eventType") MatchEventType eventType
    );
}
