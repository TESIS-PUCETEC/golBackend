package com.example.golbackend.modules.championship_managment.model;

import com.example.golbackend.modules.championship_managment.model.Championship.ChampionshipStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChampionshipUpdateRequest {

    private String championshipName;
    private String championshipDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private ChampionshipStatus championshipStatus;

    private Integer minPlayersField;
    private Integer maxPlayersField;
    private Integer minPlayersRegistered;
    private Integer maxPlayersRegistered;

    private Integer yellowDoubleSuspensionMatches;
    private Integer redSuspensionMatches;
    private Integer yellowAccumulationSuspensionMatches;
    private Integer yellowAccumulationNumber;

    private Integer pointsWin;
    private Integer pointsLose;
    private Integer pointsDraw;

    private Integer maxSubstitutions;
    private Boolean reentryAllowed;

    private Integer forfeitGoalsFor;
    private Integer forfeitGoalsAgainst;
    private BigDecimal noShowFineAmount;

    private Boolean resetCardsGroup;
    private Boolean resetCardsQuarterfinal;
    private Boolean resetCardsSemifinal;
    private Boolean resetCardsFinal;
}
