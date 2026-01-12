package com.example.golbackend.modules.championship_managment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "championship")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Championship {

    public enum ChampionshipStatus {
        PENDING, ONGOING, FINISHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "championship_id")
    private Long championshipId;

    @NotBlank(message = "El nombre del campeonato es obligatorio")
    @Column(name = "name", nullable = false, length = 100)
    private String championshipName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String championshipDescription;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ChampionshipStatus championshipStatus;

    @Min(0)
    @Column(name = "min_players_field", nullable = false)
    private int minPlayersField;

    @Min(0)
    @Column(name = "max_players_field", nullable = false)
    private int maxPlayersField;

    @Min(0)
    @Column(name = "min_players_registered", nullable = false)
    private int minPlayersRegistered;

    @Min(0)
    @Column(name = "max_players_registered", nullable = false)
    private int maxPlayersRegistered;

    @Min(0)
    @Column(name = "yellow_double_suspension_matches")
    private int yellowDoubleSuspensionMatches;

    @Min(0)
    @Column(name = "red_card_suspension_matches")
    private int redSuspensionMatches;

    @Min(0)
    @Column(name = "yellow_accumulation_suspension_matches")
    private int yellowAccumulationSuspensionMatches;

    @Min(0)
    @Column(name = "yellow_accumulation_number")
    private int yellowAccumulationNumber;

    @Column(name = "points_win")
    private int pointsWin;

    @Column(name = "points_loss")
    private int pointsLose;

    @Column(name = "points_draw")
    private int pointsDraw;

    @Min(0)
    @Column(name = "max_substitutions", nullable = false)
    private int maxSubstitutions;

    @Column(name = "reentry_allowed", nullable = false)
    private boolean reentryAllowed;

    @Min(0)
    @Column(name = "forfeit_goals_for", nullable = false)
    private int forfeitGoalsFor;

    @Min(0)
    @Column(name = "forfeit_goals_against", nullable = false)
    private int forfeitGoalsAgainst;

    @Min(0)
    @Column(name = "no_show_fine_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal noShowFineAmount;

    @Column(name = "reset_cards_group", nullable = false)
    private boolean resetCardsGroup;

    @Column(name = "reset_cards_quarterfinal", nullable = false)
    private boolean resetCardsQuarterfinal;

    @Column(name = "reset_cards_semifinal", nullable = false)
    private boolean resetCardsSemifinal;

    @Column(name = "reset_cards_final", nullable = false)
    private boolean resetCardsFinal;
}
