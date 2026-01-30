package com.example.golbackend.modules.match.controller;

import com.example.golbackend.modules.match.dto.BulkCreateMatchesDto;
import com.example.golbackend.modules.match.dto.MatchDto;
import com.example.golbackend.modules.match.dto.MatchResponseDto;
import com.example.golbackend.modules.match.dto.UpdateMatchResultDto;
import com.example.golbackend.modules.match.model.Match;
import com.example.golbackend.modules.match.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping
    public ResponseEntity<?> createMatch(@RequestBody MatchDto matchDto) {
        Match newMatch = matchService.createMatch(matchDto);
        return new ResponseEntity<>(matchService.toDto(newMatch), HttpStatus.CREATED);
    }

    @PostMapping("/multiple")
    public ResponseEntity<?> createMatchesBulk(@RequestBody BulkCreateMatchesDto bulkDto) {
        try {
            var created = matchService.createMatchesBulk(bulkDto);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/phase/{phaseId}")
    public ResponseEntity<List<MatchResponseDto>> getMatchesByPhase(@PathVariable Long phaseId) {
        return ResponseEntity.ok(
                matchService.getMatchesByPhase(phaseId).stream().map(matchService::toDto).toList()
        );
    }

    @PutMapping("/{matchId}/result")
    public ResponseEntity<?> updateMatchResult(@PathVariable Long matchId, @RequestBody UpdateMatchResultDto resultDto) {
        Match updated = matchService.updateMatchResult(matchId, resultDto);
        return ResponseEntity.ok(matchService.toDto(updated));
    }

}
