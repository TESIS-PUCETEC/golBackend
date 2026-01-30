package com.example.golbackend.modules.match_event.model;

import com.example.golbackend.modules.match.model.Match;
import com.example.golbackend.modules.players.model.Player;
import com.example.golbackend.modules.team_managment.model.Team;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "match_event")
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_event_id")
    private Long matchEventId;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "related_player_id")
    private Player relatedPlayer;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "event_type", nullable = false, columnDefinition = "match_event_type")
    private MatchEventType eventType;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "period", columnDefinition = "match_period_type")
    private MatchPeriodType period;

    @Column(name = "occurred_at")
    private OffsetDateTime occurredAt;

    @Column(name = "clock_minute")
    private Integer clockMinute;

    @Column(name = "clock_second")
    private Integer clockSecond;

    @Column(name = "clock_seconds")
    private Integer clockSeconds;

    @Column(name = "shootout_kick_order")
    private Integer shootoutKickOrder;

    @Column(name = "shootout_round")
    private Integer shootoutRound;

    @Column(name = "penalty_scored")
    private Boolean penaltyScored;

    @Column(name = "notes")
    private String notes;
}
