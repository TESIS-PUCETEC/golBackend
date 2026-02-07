package com.example.golbackend.modules.players.services;

import com.example.golbackend.modules.players.dto.PlayerDto;
import com.example.golbackend.modules.players.model.Player;
import com.example.golbackend.modules.players.repositories.PlayerRepository;
import com.example.golbackend.modules.team_managment.model.Team;
import com.example.golbackend.modules.team_managment.repositories.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    public PlayerDto createPlayer(PlayerDto dto) {
        Player player = new Player();

        // teamId opcional (free agent)
        if (dto.getTeamId() != null) {
            Team team = teamRepository.findById(dto.getTeamId())
                    .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + dto.getTeamId()));
            player.setTeam(team);
        } else {
            player.setTeam(null);
        }

        player.setFirstName(dto.getFirstName().trim());
        player.setLastName(dto.getLastName().trim());
        player.setIdCard(dto.getIdCard());
        player.setBirthDate(dto.getBirthDate());
        player.setEmail(dto.getEmail());
        player.setPosition(dto.getPosition());
        player.setShirtNumber(dto.getShirtNumber());
        if (dto.getPhotoUrl() != null && !dto.getPhotoUrl().isBlank()) {
            player.setPhotoUrl(dto.getPhotoUrl().trim());
        }

        // Si no viene status y no tiene equipo => FREE_AGENT
        if (dto.getStatus() != null) {
            player.setStatus(dto.getStatus());
        } else if (dto.getTeamId() == null) {
            player.setStatus(Player.PlayerStatus.FREE_AGENT);
        }

        Player saved = playerRepository.save(player);
        return toDto(saved);
    }

    public List<PlayerDto> getPlayers(Long teamId, Player.PlayerStatus status) {
        if (teamId != null) {
            return playerRepository.findByTeam_Id(teamId)
                    .stream()
                    .map(this::toDto)
                    .toList();
        }
        if (status != null) {
            return playerRepository.findByStatus(status)
                    .stream()
                    .map(this::toDto)
                    .toList();
        }
        return playerRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }
    

    public PlayerDto assignToTeam(Long playerId, Long teamId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found: " + playerId));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found: " + teamId));

        player.setTeam(team);

        if (player.getStatus() == Player.PlayerStatus.FREE_AGENT) {
            player.setStatus(Player.PlayerStatus.ACTIVE);
        }

        return toDto(playerRepository.save(player));
    }


    public PlayerDto releaseFromTeam(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found: " + playerId));

        player.setTeam(null);
        player.setStatus(Player.PlayerStatus.FREE_AGENT);

        return toDto(playerRepository.save(player));
    }

    private PlayerDto toDto(Player p) {
        PlayerDto dto = new PlayerDto();
        dto.setPlayerId(p.getPlayerId());
        dto.setTeamId(p.getTeam() != null ? p.getTeam().getTeamId() : null);
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setIdCard(p.getIdCard());
        dto.setBirthDate(p.getBirthDate());
        dto.setEmail(p.getEmail());
        dto.setPosition(p.getPosition());
        dto.setShirtNumber(p.getShirtNumber());
        dto.setStatus(p.getStatus());
        dto.setPhotoUrl(p.getPhotoUrl());

        return dto;
    }
}
