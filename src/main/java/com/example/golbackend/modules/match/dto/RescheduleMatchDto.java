package com.example.golbackend.modules.match.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RescheduleMatchDto {
    private LocalDateTime matchDate;
    private String fieldName;
    private String refereeName;
}
