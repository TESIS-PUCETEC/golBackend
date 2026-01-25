package com.example.golbackend.modules.phase_team.repositories;

import com.example.golbackend.modules.phase_team.model.PhaseTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhaseTeamRepository extends JpaRepository<PhaseTeam, Long> {

    List<PhaseTeam> findByPhasePhaseId(Long phaseId);

    boolean existsByPhasePhaseIdAndTeamId(Long phaseId, Long teamId);

    Optional<PhaseTeam> findByPhasePhaseIdAndTeamId(Long phaseId, Long teamId);
}
