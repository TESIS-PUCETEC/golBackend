package com.example.golbackend.modules.match_official_report.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MatchOfficialReportResponseDto {
    private Long officialReportId;
    private Long matchId;

    private String role;
    private String officialName;
    private String reportText;

    private Integer captainBandPresent;
    private Integer matchBallPresent;
    private Integer crowdSanction;
    private Integer refereeRating;

    private LocalDateTime createdAt;
}
