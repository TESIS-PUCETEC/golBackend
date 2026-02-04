package com.example.golbackend.modules.standing.controller;

import com.example.golbackend.modules.standing.services.PhaseStandingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phases")
public class PhaseStandingsController {

    private final PhaseStandingsService phaseStandingsService;

    public PhaseStandingsController(PhaseStandingsService phaseStandingsService) {
        this.phaseStandingsService = phaseStandingsService;
    }

    @GetMapping("/{phaseId}/standings")
    public ResponseEntity<Object> getStandings(@PathVariable Long phaseId) {
        return ResponseEntity.ok(phaseStandingsService.getStandingsView(phaseId));
    }
}
