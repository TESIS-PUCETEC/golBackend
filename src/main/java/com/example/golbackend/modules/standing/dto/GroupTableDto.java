package com.example.golbackend.modules.standing.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupTableDto {
    private String groupIdentifier;
    private List<StandingRowDto> table;
}
