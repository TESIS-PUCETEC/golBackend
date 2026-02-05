package com.example.golbackend.modules.match.repositories;

import com.example.golbackend.modules.match.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByPhasePhaseId(Long phaseId);

    List<Match> findByPhasePhaseIdAndStatus(Long phaseId, String status);

    List<Match> findByPhasePhaseIdAndGroupIdentifier(Long phaseId, String groupIdentifier);

    List<Match> findByPhasePhaseIdAndRoundNumber(Long phaseId, Integer roundNumber);

    List<Match> findByPhasePhaseIdAndBracketCode(Long phaseId, String bracketCode);

    List<Match> findByMatchDateBetween(LocalDateTime from, LocalDateTime to);

    List<Match> findByMatchDateBetweenOrderByMatchDateAsc(LocalDateTime from, LocalDateTime to);

}
