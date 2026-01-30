package com.example.golbackend.modules.match_official_report.services;

import com.example.golbackend.modules.match.model.Match;
import com.example.golbackend.modules.match.repositories.MatchRepository;
import com.example.golbackend.modules.match_official_report.dto.MatchOfficialReportDto;
import com.example.golbackend.modules.match_official_report.dto.MatchOfficialReportResponseDto;
import com.example.golbackend.modules.match_official_report.model.MatchOfficialReport;
import com.example.golbackend.modules.match_official_report.repositories.MatchOfficialReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchOfficialReportService {

    @Autowired
    private MatchOfficialReportRepository reportRepository;

    @Autowired
    private MatchRepository matchRepository;

    public MatchOfficialReportResponseDto createReport(Long matchId, MatchOfficialReportDto dto) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        String role = normalizeRole(dto.getRole());

        MatchOfficialReport report = new MatchOfficialReport();
        report.setMatch(match);
        report.setRole(role);
        report.setOfficialName(dto.getOfficialName());
        report.setReportText(dto.getReportText());

        Integer captainBandPresent = dto.getCaptainBandPresent() != null ? dto.getCaptainBandPresent() : 0;
        Integer matchBallPresent = dto.getMatchBallPresent() != null ? dto.getMatchBallPresent() : 0;
        Integer crowdSanction = dto.getCrowdSanction() != null ? dto.getCrowdSanction() : 0;

        validateZeroOne(captainBandPresent, "captainBandPresent");
        validateZeroOne(matchBallPresent, "matchBallPresent");
        validateZeroOne(crowdSanction, "crowdSanction");

        report.setCaptainBandPresent(captainBandPresent);
        report.setMatchBallPresent(matchBallPresent);
        report.setCrowdSanction(crowdSanction);

        if ("VOCAL".equalsIgnoreCase(role)) {
            Integer rating = dto.getRefereeRating();
            if (rating == null) {
                throw new RuntimeException("refereeRating is required when role is VOCAL");
            }
            if (rating < 1 || rating > 10) {
                throw new RuntimeException("refereeRating must be between 1 and 10");
            }
            report.setRefereeRating(rating);
        } else {
            report.setRefereeRating(null);
        }

        MatchOfficialReport saved = reportRepository.save(report);
        return toResponseDto(saved);
    }

    public List<MatchOfficialReportResponseDto> getReportsByMatch(Long matchId) {
        return reportRepository.findByMatchMatchId(matchId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    private void validateZeroOne(Integer value, String fieldName) {
        if (value == null || (value != 0 && value != 1)) {
            throw new RuntimeException(fieldName + " must be 0 or 1");
        }
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            throw new RuntimeException("role is required");
        }

        String r = role.trim().toUpperCase();

        if ("REFEREE".equals(r)) {
            r = "ARBITRO";
        }

        if (!r.equals("VOCAL") && !r.equals("VEEDOR") && !r.equals("ARBITRO")) {
            throw new RuntimeException("Invalid role. Allowed values: VOCAL, VEEDOR, ARBITRO");
        }

        return r;
    }

    private MatchOfficialReportResponseDto toResponseDto(MatchOfficialReport r) {
        MatchOfficialReportResponseDto dto = new MatchOfficialReportResponseDto();
        dto.setOfficialReportId(r.getOfficialReportId());
        dto.setMatchId(r.getMatch().getMatchId());

        dto.setRole(r.getRole());
        dto.setOfficialName(r.getOfficialName());
        dto.setReportText(r.getReportText());

        dto.setCaptainBandPresent(r.getCaptainBandPresent());
        dto.setMatchBallPresent(r.getMatchBallPresent());
        dto.setCrowdSanction(r.getCrowdSanction());
        dto.setRefereeRating(r.getRefereeRating());

        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
