package com.example.golbackend.modules.match.model;

import com.example.golbackend.modules.championship_managment.model.Championship;
import com.example.golbackend.modules.phase.model.Phase;
import com.example.golbackend.modules.team_managment.model.Team;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long matchId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "phase_id", nullable = false)
    private Phase phase;

    @ManyToOne
    @JoinColumn(name = "championship_id")
    private Championship championship;

    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "home_penalties")
    private Integer homePenalties;

    @Column(name = "away_penalties")
    private Integer awayPenalties;

    @Column(name = "status")
    private String status = "SCHEDULED";

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "referee_name")
    private String refereeName;

    @Column(name = "round_number")
    private Integer roundNumber;

    @Column(name = "group_identifier")
    private String groupIdentifier;

    @Column(name = "bracket_code")
    private String bracketCode;

    @Column(name = "leg")
    private Integer leg = 1;

    @ManyToOne
    @JoinColumn(name = "winner_goes_to_match_id")
    private Match winnerGoesToMatch;

    @ManyToOne
    @JoinColumn(name = "loser_goes_to_match_id")
    private Match loserGoesToMatch;

    @Column(name = "matchday_number", nullable = false)
    private Integer matchdayNumber = 1;
}
