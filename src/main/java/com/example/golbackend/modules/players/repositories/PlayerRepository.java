package com.example.golbackend.modules.players.repositories;

import com.example.golbackend.modules.players.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeam_Id(Long teamId);
    List<Player> findByTeamIsNull();
    List<Player> findByStatus(Player.PlayerStatus status);
}
