package com.example.golbackend.modules.match_official_report.model;

import com.example.golbackend.modules.match.model.Match;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "match_official_report")
public class MatchOfficialReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "official_report_id")
    private Long officialReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Column(name = "official_name", nullable = false, length = 100)
    private String officialName;

    @Column(name = "report_text", nullable = false, columnDefinition = "TEXT")
    private String reportText;

    @Column(name = "captain_band_present")
    private Integer captainBandPresent = 0;

    @Column(name = "match_ball_present")
    private Integer matchBallPresent = 0;

    @Column(name = "crowd_sanction")
    private Integer crowdSanction = 0;

    @Column(name = "referee_rating")
    private Integer refereeRating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
