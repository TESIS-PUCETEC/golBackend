package com.example.golbackend.modules.standing.services;

import com.example.golbackend.modules.standing.model.Standing;
import com.example.golbackend.modules.standing.repositories.StandingRepository;
import com.example.golbackend.modules.tie_break_criterion.model.TieBreakCriterion;
import com.example.golbackend.modules.tie_break_criterion.repositories.TieBreakCriterionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandingService {

    @Autowired
    private StandingRepository standingRepository;
    @Autowired
    private TieBreakCriterionRepository tieBreakCriterionRepository;

    public List<Standing> getStandingsByPhase(Long phaseId) {
        List<TieBreakCriterion> criteria = tieBreakCriterionRepository.findByPhasePhaseIdOrderByPriorityOrderAsc(phaseId);

        if (criteria.isEmpty()) {
            // Comportamiento por defecto si no hay criterios definidos
            return standingRepository.findByPhasePhaseIdOrderByPointsDescGoalDifferenceDescGoalsForDesc(phaseId);
        }

        List<Sort.Order> orders = criteria.stream()
                .map(criterion -> {
                    // Mapea el nombre del criterio al nombre del campo en la entidad Standing
                    String propertyName = mapCriterionToPropertyName(criterion.getCriterion());
                    return Sort.Order.desc(propertyName);
                })
                .collect(Collectors.toList());

        return standingRepository.findByPhasePhaseId(phaseId, Sort.by(orders));
    }

    private String mapCriterionToPropertyName(String criterion) {
        switch (criterion.toUpperCase()) {
            case "POINTS":
                return "points";
            case "GOAL_DIFFERENCE":
                return "goalDifference";
            case "GOALS_FOR":
                return "goalsFor";
            default:
                return "points";
        }
    }
}