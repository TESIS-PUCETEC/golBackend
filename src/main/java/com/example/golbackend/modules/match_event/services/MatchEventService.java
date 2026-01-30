package com.example.golbackend.modules.match_event.services;

import com.example.golbackend.modules.match.model.Match;
import com.example.golbackend.modules.match.repositories.MatchRepository;
import com.example.golbackend.modules.match_event.dto.MatchEventDto;
import com.example.golbackend.modules.match_event.model.MatchEvent;
import com.example.golbackend.modules.match_event.model.MatchEventType;
import com.example.golbackend.modules.match_event.repositories.MatchEventRepository;
import com.example.golbackend.modules.players.model.Player;
import com.example.golbackend.modules.players.repositories.PlayerRepository;
import com.example.golbackend.modules.team_managment.model.Team;
import com.example.golbackend.modules.team_managment.repositories.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchEventService {

    @Autowired private MatchEventRepository matchEventRepository;
    @Autowired private MatchRepository matchRepository;
    @Autowired private PlayerRepository playerRepository;

    @Autowired(required = false)
    private TeamRepository teamRepository;

    public MatchEvent createMatchEvent(Long matchId, MatchEventDto dto) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        if (dto == null) throw new RuntimeException("body cannot be null");
        if (dto.getEventType() == null) throw new RuntimeException("eventType is required");

        MatchEvent event = new MatchEvent();
        event.setMatch(match);

        applyDto(event, dto);

        return matchEventRepository.save(event);
    }

    public List<MatchEvent> getEventsByMatch(Long matchId) {
        return matchEventRepository.findByMatchMatchIdOrderByPeriodAscClockSecondsAsc(matchId);
    }

    @Transactional
    public MatchEvent updateMatchEvent(Long matchId, Long eventId, MatchEventDto dto) {
        if (dto == null) throw new RuntimeException("body cannot be null");
        if (dto.getEventType() == null) throw new RuntimeException("eventType is required");

        matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        MatchEvent event = matchEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("MatchEvent not found with id: " + eventId));

        if (!event.getMatch().getMatchId().equals(matchId)) {
            throw new RuntimeException("MatchEvent " + eventId + " does not belong to match " + matchId);
        }

        applyDto(event, dto);

        return matchEventRepository.save(event);
    }

    private void applyDto(MatchEvent event, MatchEventDto dto) {

        MatchEventType eventType = dto.getEventType();
        event.setEventType(eventType);

        event.setPeriod(dto.getPeriod());

        event.setOccurredAt(dto.getOccurredAt());

        Integer minute = dto.getClockMinute();
        Integer second = dto.getClockSecond();
        Integer seconds = dto.getClockSeconds();

        if (minute != null && minute < 0) throw new RuntimeException("clockMinute must be >= 0");
        if (second != null && (second < 0 || second > 59)) throw new RuntimeException("clockSecond must be between 0 and 59");

        if (seconds == null && minute != null && second != null) {
            seconds = minute * 60 + second;
        }
        if (seconds != null && seconds < 0) throw new RuntimeException("clockSeconds must be >= 0");

        event.setClockMinute(minute);
        event.setClockSecond(second);
        event.setClockSeconds(seconds);

        if (dto.getPlayerId() != null) {
            Player player = playerRepository.findById(dto.getPlayerId())
                    .orElseThrow(() -> new RuntimeException("Player not found with id: " + dto.getPlayerId()));
            event.setPlayer(player);
        } else {
            event.setPlayer(null);
        }

        if (dto.getRelatedPlayerId() != null) {
            Player rp = playerRepository.findById(dto.getRelatedPlayerId())
                    .orElseThrow(() -> new RuntimeException("Related player not found with id: " + dto.getRelatedPlayerId()));
            event.setRelatedPlayer(rp);
        } else {
            event.setRelatedPlayer(null);
        }

        if (dto.getTeamId() != null) {
            if (teamRepository == null) {
                throw new RuntimeException("TeamRepository not configured but teamId was provided");
            }
            Team team = teamRepository.findById(dto.getTeamId())
                    .orElseThrow(() -> new RuntimeException("Team not found with id: " + dto.getTeamId()));
            event.setTeam(team);
        } else {
            event.setTeam(null);
        }

        Integer kickOrder = dto.getShootoutKickOrder();
        Integer round = dto.getShootoutRound();

        if (kickOrder != null && kickOrder <= 0) throw new RuntimeException("shootoutKickOrder must be > 0");
        if (round != null && round <= 0) throw new RuntimeException("shootoutRound must be > 0");

        event.setShootoutKickOrder(kickOrder);
        event.setShootoutRound(round);

        event.setPenaltyScored(dto.getPenaltyScored());

        event.setNotes(dto.getNotes());


        if (eventType == MatchEventType.SUBSTITUTION_IN || eventType == MatchEventType.SUBSTITUTION_OUT) {
            if (event.getPlayer() == null || event.getRelatedPlayer() == null) {
                throw new RuntimeException("Substitution requires playerId and relatedPlayerId");
            }
        }

        if (eventType == MatchEventType.PENALTY_KICK) {
            if (event.getPlayer() == null) throw new RuntimeException("PENALTY_KICK requires playerId");
            if (dto.getPenaltyScored() == null) throw new RuntimeException("PENALTY_KICK requires penaltyScored (true/false)");
            if (kickOrder == null) throw new RuntimeException("PENALTY_KICK requires shootoutKickOrder");
        }

        if (eventType == MatchEventType.GOAL || eventType == MatchEventType.ASSIST
                || eventType == MatchEventType.YELLOW_CARD || eventType == MatchEventType.RED_CARD) {
            if (event.getPlayer() == null) {
                throw new RuntimeException(eventType + " requires playerId");
            }
        }

        if (eventType == MatchEventType.PERIOD_START || eventType == MatchEventType.PERIOD_END) {
            if (event.getPeriod() == null) {
                throw new RuntimeException(eventType + " requires period");
            }
        }
    }
}
