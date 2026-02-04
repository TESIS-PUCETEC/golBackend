package com.example.golbackend.modules.match_player.services;

import com.example.golbackend.modules.match.model.Match;
import com.example.golbackend.modules.match.repositories.MatchRepository;
import com.example.golbackend.modules.match_player.dto.MatchPlayerDto;
import com.example.golbackend.modules.match_player.dto.MatchPlayerResponseDto;
import com.example.golbackend.modules.match_player.model.MatchPlayer;
import com.example.golbackend.modules.match_player.repositories.MatchPlayerRepository;
import com.example.golbackend.modules.players.model.Player;
import com.example.golbackend.modules.players.repositories.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchPlayerService {

    @Autowired
    private MatchPlayerRepository matchPlayerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public List<MatchPlayerResponseDto> addPlayersToMatch(Long matchId, List<MatchPlayerDto> playerDtos) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        if (playerDtos == null || playerDtos.isEmpty()) {
            throw new RuntimeException("players list cannot be null or empty");
        }

        Set<Long> seen = new HashSet<>();
        for (MatchPlayerDto dto : playerDtos) {
            if (dto.getPlayerId() == null) {
                throw new RuntimeException("playerId is required for all items");
            }
            if (!seen.add(dto.getPlayerId())) {
                throw new RuntimeException("Duplicate playerId in request: " + dto.getPlayerId());
            }
        }

        List<Long> playerIds = playerDtos.stream().map(MatchPlayerDto::getPlayerId).toList();

        Map<Long, Player> playerMap = playerRepository.findAllById(playerIds).stream()
                .collect(Collectors.toMap(Player::getPlayerId, p -> p));

        for (Long pid : playerIds) {
            if (!playerMap.containsKey(pid)) {
                throw new RuntimeException("Player not found with id: " + pid);
            }
        }

        Set<Long> alreadyAssigned = matchPlayerRepository.findByMatchMatchId(matchId).stream()
                .map(mp -> mp.getPlayer().getPlayerId())
                .collect(Collectors.toSet());

        List<MatchPlayer> toSave = new ArrayList<>();

        for (MatchPlayerDto dto : playerDtos) {
            if (alreadyAssigned.contains(dto.getPlayerId())) {
                continue;
            }

            MatchPlayer matchPlayer = new MatchPlayer();
            matchPlayer.setMatch(match);
            matchPlayer.setPlayer(playerMap.get(dto.getPlayerId()));
            matchPlayer.setStarting(dto.isStarting());
            matchPlayer.setMinutesPlayed(dto.getMinutesPlayed());
            matchPlayer.setGoals(dto.getGoals());
            matchPlayer.setAssists(dto.getAssists());
            matchPlayer.setYellowCards(dto.getYellowCards());
            matchPlayer.setRedCards(dto.getRedCards());

            toSave.add(matchPlayer);
        }

        List<MatchPlayer> saved = matchPlayerRepository.saveAll(toSave);
        return saved.stream().map(this::toDto).toList();
    }

    public List<MatchPlayerResponseDto> getPlayersInMatch(Long matchId) {
        return matchPlayerRepository.findByMatchMatchId(matchId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public List<MatchPlayerResponseDto> updatePlayersInMatch(Long matchId, List<MatchPlayerDto> playerDtos) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        if (playerDtos == null) {
            throw new RuntimeException("players list cannot be null");
        }

        Set<Long> seen = new HashSet<>();
        for (MatchPlayerDto dto : playerDtos) {
            if (dto.getPlayerId() == null) {
                throw new RuntimeException("playerId is required for all items");
            }
            if (!seen.add(dto.getPlayerId())) {
                throw new RuntimeException("Duplicate playerId in request: " + dto.getPlayerId());
            }
        }

        List<MatchPlayer> existing = matchPlayerRepository.findByMatchMatchId(matchId);
        Map<Long, MatchPlayer> existingByPlayerId = existing.stream()
                .collect(Collectors.toMap(mp -> mp.getPlayer().getPlayerId(), mp -> mp));

        // Players del request
        List<Long> playerIds = playerDtos.stream()
                .map(MatchPlayerDto::getPlayerId)
                .toList();

        Map<Long, Player> playerMap = playerRepository.findAllById(playerIds).stream()
                .collect(Collectors.toMap(Player::getPlayerId, p -> p));

        for (Long pid : playerIds) {
            if (!playerMap.containsKey(pid)) {
                throw new RuntimeException("Player not found with id: " + pid);
            }
        }

        List<MatchPlayer> toSave = new ArrayList<>();
        for (MatchPlayerDto dto : playerDtos) {
            MatchPlayer mp = existingByPlayerId.get(dto.getPlayerId());

            if (mp == null) {
                mp = new MatchPlayer();
                mp.setMatch(match);
                mp.setPlayer(playerMap.get(dto.getPlayerId()));
            }

            mp.setStarting(dto.isStarting());
            mp.setMinutesPlayed(dto.getMinutesPlayed());
            mp.setGoals(dto.getGoals());
            mp.setAssists(dto.getAssists());
            mp.setYellowCards(dto.getYellowCards());
            mp.setRedCards(dto.getRedCards());

            toSave.add(mp);
        }

        Set<Long> requestedIds = new HashSet<>(playerIds);
        List<MatchPlayer> toDelete = existing.stream()
                .filter(mp -> !requestedIds.contains(mp.getPlayer().getPlayerId()))
                .toList();

        if (!toDelete.isEmpty()) {
            matchPlayerRepository.deleteAll(toDelete);
        }

        List<MatchPlayer> saved = matchPlayerRepository.saveAll(toSave);
        return saved.stream().map(this::toDto).toList();
    }

    private MatchPlayerResponseDto toDto(MatchPlayer mp) {
        return MatchPlayerResponseDto.builder()
                .matchPlayerId(mp.getMatchPlayerId())
                .matchId(mp.getMatch().getMatchId())
                .playerId(mp.getPlayer().getPlayerId())
                .playerFirstName(mp.getPlayer().getFirstName())
                .playerLastName(mp.getPlayer().getLastName())
                .position(mp.getPlayer().getPosition())
                .shirtNumber(mp.getPlayer().getShirtNumber())
                .teamId(mp.getPlayer().getTeam() != null ? mp.getPlayer().getTeam().getTeamId() : null)
                .teamName(mp.getPlayer().getTeam() != null ? mp.getPlayer().getTeam().getName() : null)
                .starting(mp.isStarting())
                .minutesPlayed(mp.getMinutesPlayed())
                .goals(mp.getGoals())
                .assists(mp.getAssists())
                .yellowCards(mp.getYellowCards())
                .redCards(mp.getRedCards())
                .build();
    }
}
