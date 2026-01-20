package com.example.golbackend.modules.players.model;

import com.example.golbackend.modules.team_managment.model.Team;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = true)
    private Team team;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "id_card", unique = true, length = 20)
    private String idCard;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "position", length = 50)
    private String position;

    @Column(name = "shirt_number")
    private Integer shirtNumber;

    @Column(name = "email", unique = true, length = 150)
    private String email;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PlayerStatus status = PlayerStatus.ACTIVE;

    public enum PlayerStatus {
        ACTIVE,
        SUSPENDED,
        INJURED,
        FREE_AGENT
    }
}
