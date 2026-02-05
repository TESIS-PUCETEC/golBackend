package com.example.golbackend.modules.standing.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KnockoutBracketResponseDto {
    private Long phaseId;
    private String phaseName;
    private String type;
    private String matchFormat;
    private List<KnockoutRoundDto> rounds;
}
