package com.example.golbackend.modules.phase_team.services;

import com.example.golbackend.modules.phase.model.Phase;
import com.example.golbackend.modules.phase.repositories.PhaseRepository;
import com.example.golbackend.modules.phase_team.dto.PhaseTeamDto;
import com.example.golbackend.modules.phase_team.dto.PhaseTeamResponseDto;
import com.example.golbackend.modules.phase_team.model.PhaseTeam;
import com.example.golbackend.modules.phase_team.repositories.PhaseTeamRepository;
import com.example.golbackend.modules.team_managment.model.Team;
import com.example.golbackend.modules.team_managment.repositories.TeamRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PhaseTeamService {

    private final PhaseTeamRepository phaseTeamRepository;
    private final PhaseRepository phaseRepository;
    private final TeamRepository teamRepository;

    public PhaseTeamService(
            PhaseTeamRepository phaseTeamRepository,
            PhaseRepository phaseRepository,
            TeamRepository teamRepository
    ) {
        this.phaseTeamRepository = phaseTeamRepository;
        this.phaseRepository = phaseRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public PhaseTeamResponseDto addTeamToPhase(Long phaseId, PhaseTeamDto dto) {
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found with id: " + phaseId));

        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + dto.getTeamId()));

        if (phaseTeamRepository.existsByPhasePhaseIdAndTeamId(phaseId, dto.getTeamId())) {
            throw new RuntimeException("This team is already registered in this phase.");
        }

        PhaseTeam pt = new PhaseTeam();
        pt.setPhase(phase);
        pt.setTeam(team);
        pt.setGroupIdentifier(dto.getGroupIdentifier());
        pt.setInitialPoints(dto.getInitialPoints() != null ? dto.getInitialPoints() : 0);
        pt.setSeed(dto.getSeed());
        pt.setQualifiedPosition(dto.getQualifiedPosition());

        PhaseTeam saved = phaseTeamRepository.save(pt);

        return PhaseTeamResponseDto.builder()
                .phaseTeamId(saved.getPhaseTeamId())
                .phaseId(saved.getPhase().getPhaseId())
                .teamId(saved.getTeam().getTeamId())
                .groupIdentifier(saved.getGroupIdentifier())
                .initialPoints(saved.getInitialPoints())
                .seed(saved.getSeed())
                .qualifiedPosition(saved.getQualifiedPosition())
                .build();
    }


    @Transactional(readOnly = true)
    public List<PhaseTeamResponseDto> getTeamsInPhase(Long phaseId) {
        return phaseTeamRepository.findByPhasePhaseId(phaseId).stream()
                .map(pt -> PhaseTeamResponseDto.builder()
                        .phaseTeamId(pt.getPhaseTeamId())
                        .phaseId(pt.getPhase().getPhaseId())
                        .teamId(pt.getTeam().getTeamId())
                        .teamName(pt.getTeam().getName())
                        .groupIdentifier(pt.getGroupIdentifier())
                        .initialPoints(pt.getInitialPoints())
                        .seed(pt.getSeed())
                        .qualifiedPosition(pt.getQualifiedPosition())
                        .build()
                )
                .toList();
    }
}
