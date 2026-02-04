package com.example.golbackend.modules.standing.model;

import com.example.golbackend.modules.phase.model.Phase;
import com.example.golbackend.modules.team_managment.model.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "standing",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_standing_phase_team", columnNames = {"phase_id", "team_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Standing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "standing_id")
    private Long standingId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phase_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Phase phase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Team team;

    @Builder.Default private Integer matchesPlayed = 0;
    @Builder.Default private Integer wins = 0;
    @Builder.Default private Integer draws = 0;
    @Builder.Default private Integer losses = 0;
    @Builder.Default private Integer goalsFor = 0;
    @Builder.Default private Integer goalsAgainst = 0;
    @Builder.Default private Integer goalDifference = 0;
    @Builder.Default private Integer points = 0;
}
