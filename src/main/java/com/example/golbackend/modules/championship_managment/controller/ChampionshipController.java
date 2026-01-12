package com.example.golbackend.modules.championship_managment.controller;

import com.example.golbackend.modules.championship_managment.model.ChampionshipUpdateRequest;
import com.example.golbackend.modules.championship_managment.model.Championship;
import com.example.golbackend.modules.championship_managment.services.ChampionshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/championship")
public class ChampionshipController {

    private final ChampionshipService championshipService;

    public ChampionshipController(ChampionshipService championshipService) {
        this.championshipService = championshipService;
    }

    @PostMapping
    public ResponseEntity<Championship> createChampionship(
            @Valid @RequestBody Championship championship
    ) {
        Championship saved = championshipService.createChampionship(championship);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Championship>> getChampionships() {
        return ResponseEntity.ok(championshipService.listChampionship());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Championship> getChampionshipByID(@PathVariable Long id) {
        return ResponseEntity.ok(championshipService.getChampionshipById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Championship> patchChampionship(
            @PathVariable Long id,
            @RequestBody ChampionshipUpdateRequest request
    ) {
        Championship updated = championshipService.updateChampionship(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChampionship(@PathVariable Long id) {
        championshipService.deleteChampionship(id);
        return ResponseEntity.noContent().build();
    }
}
