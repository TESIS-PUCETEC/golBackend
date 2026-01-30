package com.example.golbackend.modules.match_event.controller;

import com.example.golbackend.modules.match_event.dto.MatchEventDto;
import com.example.golbackend.modules.match_event.model.MatchEvent;
import com.example.golbackend.modules.match_event.services.MatchEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches/{matchId}/events")
public class MatchEventController {

    @Autowired
    private MatchEventService matchEventService;

    @PostMapping
    public ResponseEntity<?> createEvent(@PathVariable Long matchId, @RequestBody MatchEventDto eventDto) {
        try {
            MatchEvent newEvent = matchEventService.createMatchEvent(matchId, eventDto);
            return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<MatchEvent>> getEvents(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchEventService.getEventsByMatch(matchId));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long matchId,
                                         @PathVariable Long eventId,
                                         @RequestBody MatchEventDto eventDto) {
        try {
            MatchEvent updated = matchEventService.updateMatchEvent(matchId, eventId, eventDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
