package com.example.golbackend.modules.standing.controller;

import com.example.golbackend.modules.standing.dto.StandingResponseDto;
import com.example.golbackend.modules.standing.model.Standing;
import com.example.golbackend.modules.standing.services.StandingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phases")
public class StandingController {

    private final StandingService standingService;

    public StandingController(StandingService standingService) {
        this.standingService = standingService;
    }


    @PostMapping("/{phaseId}/standings/recalculate")
    public ResponseEntity<List<StandingResponseDto>> recalculate(@PathVariable Long phaseId) {
        List<Standing> list = standingService.recalculatePhase(phaseId);
        return ResponseEntity.ok(list.stream().map(StandingResponseDto::from).toList());
    }
}
