package com.example.golbackend.modules.match.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkCreateMatchesDto {

    private Long phaseId;
    private Long championshipId;

    private List<MatchDto> matches;
}
