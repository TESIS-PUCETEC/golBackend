package com.example.golbackend.modules.match_result.model;

import com.example.golbackend.modules.match.model.Match;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "match_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_result_id")
    private Long matchResultId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Match match;

    @Column(name = "home_goals", nullable = false)
    @Builder.Default
    private Integer homeGoals = 0;

    @Column(name = "away_goals", nullable = false)
    @Builder.Default
    private Integer awayGoals = 0;

    @Column(name = "home_penalties")
    private Integer homePenalties;

    @Column(name = "away_penalties")
    private Integer awayPenalties;

    @Column(name = "is_final", nullable = false)
    @Builder.Default
    private Boolean isFinal = false;

    @Column(name = "finalized_at")
    private OffsetDateTime finalizedAt;

    @Column(name = "recalculated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime recalculatedAt = OffsetDateTime.now();
}
