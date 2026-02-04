package com.example.golbackend.modules.match_result.controller;

import com.example.golbackend.modules.match_result.dto.MatchResultResponseDto;
import com.example.golbackend.modules.match_result.model.MatchResult;
import com.example.golbackend.modules.match_result.services.MatchResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
public class MatchResultController {

    private final MatchResultService matchResultService;

    public MatchResultController(MatchResultService matchResultService) {
        this.matchResultService = matchResultService;
    }

    @PostMapping("/{matchId}/result/recalculate")
    public ResponseEntity<MatchResultResponseDto> recalculate(@PathVariable Long matchId) {
        MatchResult result = matchResultService.recalculateFromEvents(matchId);
        return ResponseEntity.ok(MatchResultResponseDto.from(result));
    }

    @PostMapping("/{matchId}/result/finalize")
    public ResponseEntity<MatchResultResponseDto> finalizeMatch(@PathVariable Long matchId) {
        MatchResult result = matchResultService.finalizeMatch(matchId);
        return ResponseEntity.ok(MatchResultResponseDto.from(result));
    }

    @GetMapping("/{matchId}/result")
    public ResponseEntity<MatchResultResponseDto> getResult(@PathVariable Long matchId) {
        MatchResult result = matchResultService.getOrCreateFromEvents(matchId);
        return ResponseEntity.ok(MatchResultResponseDto.from(result));
    }
}
