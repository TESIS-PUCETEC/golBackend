package com.example.golbackend.modules.phase_team.controller;

import com.example.golbackend.modules.phase_team.dto.PhaseTeamDto;
import com.example.golbackend.modules.phase_team.dto.PhaseTeamResponseDto;
import com.example.golbackend.modules.phase_team.model.PhaseTeam;
import com.example.golbackend.modules.phase_team.services.PhaseTeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phases/{phaseId}/teams")
public class PhaseTeamController {

    private final PhaseTeamService phaseTeamService;

    public PhaseTeamController(PhaseTeamService phaseTeamService) {
        this.phaseTeamService = phaseTeamService;
    }

    @PostMapping
    public ResponseEntity<?> addTeamToPhase(@PathVariable Long phaseId, @RequestBody PhaseTeamDto dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(phaseTeamService.addTeamToPhase(phaseId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<PhaseTeamResponseDto>> getTeamsInPhase(@PathVariable Long phaseId) {
        return ResponseEntity.ok(phaseTeamService.getTeamsInPhase(phaseId));
    }

}
