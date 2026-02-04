package com.example.golbackend.modules.standing.repositories;

import com.example.golbackend.modules.standing.model.Standing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StandingRepository extends JpaRepository<Standing, Long> {

    List<Standing> findByPhasePhaseIdOrderByPointsDescGoalDifferenceDescGoalsForDesc(Long phaseId);

    Optional<Standing> findByPhasePhaseIdAndTeam_Id(Long phaseId, Long teamId);

    void deleteByPhasePhaseId(Long phaseId);
}
