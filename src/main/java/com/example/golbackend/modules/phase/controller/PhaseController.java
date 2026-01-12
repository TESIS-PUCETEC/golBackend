package com.example.golbackend.modules.phase.controller;

import com.example.golbackend.modules.phase.model.PhaseCreateRequest;
import com.example.golbackend.modules.phase.model.PhaseResponse;
import com.example.golbackend.modules.phase.services.PhaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/championships/{championshipId}/phases")
public class PhaseController {

    private final PhaseService phaseService;

    public PhaseController(PhaseService phaseService) {
        this.phaseService = phaseService;
    }

    @PostMapping
    public ResponseEntity<?> createPhase(@PathVariable Long championshipId,
                                         @Valid @RequestBody PhaseCreateRequest req) {
        try {
            PhaseResponse created = phaseService.createPhase(championshipId, req);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PhaseResponse>> getPhases(@PathVariable Long championshipId) {
        return ResponseEntity.ok(phaseService.getPhasesByChampionship(championshipId));
    }

    @DeleteMapping("/{phaseId}")
    public ResponseEntity<?> deletePhase(@PathVariable Long championshipId,
                                         @PathVariable Long phaseId) {
        try {
            phaseService.deletePhase(championshipId, phaseId);
            return ResponseEntity.noContent().build(); // 204
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
