package com.example.golbackend.modules.players.controller;

import com.example.golbackend.modules.players.dto.PlayerDto;
import com.example.golbackend.modules.players.model.Player;
import com.example.golbackend.modules.players.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Crear jugador (con teamId opcional)
    @PostMapping("/players")
    public ResponseEntity<PlayerDto> create(@Valid @RequestBody PlayerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(dto));
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerDto>> list(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Player.PlayerStatus status
    ) {
        return ResponseEntity.ok(playerService.getPlayers(teamId, status));
    }



    // Asignar jugador a un equipo
    @PutMapping("/players/{playerId}/assign/{teamId}")
    public ResponseEntity<PlayerDto> assign(@PathVariable Long playerId, @PathVariable Long teamId) {
        return ResponseEntity.ok(playerService.assignToTeam(playerId, teamId));
    }

    // Liberar jugador del equipo (team_id = null + status = FREE_AGENT)
    @PutMapping("/players/{playerId}/release")
    public ResponseEntity<PlayerDto> release(@PathVariable Long playerId) {
        return ResponseEntity.ok(playerService.releaseFromTeam(playerId));
    }
}
