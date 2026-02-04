package com.example.golbackend.modules.standing.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KnockoutRoundDto {
    private Integer roundNumber;
    private List<KnockoutTieDto> ties;
}
