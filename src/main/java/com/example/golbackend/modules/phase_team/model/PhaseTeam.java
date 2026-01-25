package com.example.golbackend.modules.phase_team.model;

import com.example.golbackend.modules.phase.model.Phase;
import com.example.golbackend.modules.team_managment.model.Team;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        name = "phase_team",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_phase_team_participation", columnNames = {"phase_id", "team_id"})
        }
)
public class PhaseTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phase_team_id")
    private Long phaseTeamId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phase_id", nullable = false)
    private Phase phase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "group_identifier", length = 50)
    private String groupIdentifier;

    @Column(name = "initial_points")
    private Integer initialPoints = 0;

    @Column(name = "seed")
    private Integer seed;

    @Column(name = "qualified_position")
    private Integer qualifiedPosition;
}
