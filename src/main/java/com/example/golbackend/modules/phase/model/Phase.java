package com.example.golbackend.modules.phase.model;

import com.example.golbackend.modules.championship_managment.model.Championship;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "phase",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_phase_order", columnNames = {"championship_id", "phase_order"})
        }
)
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phase_id")
    private Long phaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "championship_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Championship championship;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PhaseType type;

    @Column(name = "phase_order", nullable = false)
    private Integer phaseOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PhaseStatus status;

    @Column(name = "groups_count")
    private Integer groupsCount;

    @Column(name = "teams_per_group")
    private Integer teamsPerGroup;

    @Column(name = "qualifiers_per_group")
    private Integer qualifiersPerGroup;

    @Column(name = "best_third_qualifiers")
    private Integer bestThirdQualifiers;

    @Column(name = "carry_over_points")
    private Boolean carryOverPoints;


    @Column(name = "league_qualifiers_count")
    private Integer leagueQualifiersCount;


    @Column(name = "knockout_start_count")
    private Integer knockoutStartCount;

    @Column(name = "has_third_place_match")
    private Boolean hasThirdPlaceMatch;

    @Column(name = "away_goals_rule")
    private Boolean awayGoalsRule;

    @Enumerated(EnumType.STRING)
    @Column(name = "pairing_method", length = 20)
    private PairingMethod pairingMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_format", length = 20)
    private MatchFormat matchFormat;

    @PrePersist
    public void prePersist() {
        if (status == null) status = PhaseStatus.PENDING;

        if (type == PhaseType.GROUP) {
            if (carryOverPoints == null) carryOverPoints = false;
        }
        if (type == PhaseType.KNOCKOUT) {
            if (hasThirdPlaceMatch == null) hasThirdPlaceMatch = false;
            if (awayGoalsRule == null) awayGoalsRule = false;
        }
    }

    public enum PhaseType { GROUP, KNOCKOUT, LEAGUE }
    public enum PhaseStatus { PENDING, ONGOING, FINISHED }
    public enum MatchFormat { SINGLE_MATCH, HOME_AWAY }
    public enum PairingMethod { AUTOMATIC_BRACKET, MANUAL_DRAW, CROSS_GROUPS }
}
