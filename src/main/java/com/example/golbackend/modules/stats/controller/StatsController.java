package com.example.golbackend.modules.stats.controller;

import com.example.golbackend.modules.stats.dto.PlayerCardRow;
import com.example.golbackend.modules.stats.dto.TeamGoalsRow;
import com.example.golbackend.modules.stats.dto.TopScorerRow;
import com.example.golbackend.modules.stats.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phases")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/{phaseId}/stats/top-scorers")
    public ResponseEntity<List<TopScorerRow>> topScorers(
            @PathVariable Long phaseId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(statsService.getTopScorers(phaseId, limit));
    }

    @GetMapping("/{phaseId}/stats/yellow-cards")
    public ResponseEntity<List<PlayerCardRow>> yellowCards(
            @PathVariable Long phaseId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(statsService.getYellowCards(phaseId, limit));
    }

    @GetMapping("/{phaseId}/stats/red-cards")
    public ResponseEntity<List<PlayerCardRow>> redCards(
            @PathVariable Long phaseId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(statsService.getRedCards(phaseId, limit));
    }

    @GetMapping("/{phaseId}/stats/best-attack")
    public ResponseEntity<List<TeamGoalsRow>> bestAttack(
            @PathVariable Long phaseId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(statsService.getBestAttack(phaseId, limit));
    }
}
