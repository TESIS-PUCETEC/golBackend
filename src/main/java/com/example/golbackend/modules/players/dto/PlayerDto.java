package com.example.golbackend.modules.players.dto;

import com.example.golbackend.modules.players.model.Player;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PlayerDto {
    private Long playerId;
    private Long teamId;

    private String firstName;
    private String lastName;
    private String idCard;
    private LocalDate birthDate;
    private String position;
    private Integer shirtNumber;
    private String email;

    private String photoUrl;


    private Player.PlayerStatus status;
}
