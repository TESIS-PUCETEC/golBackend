package com.example.golbackend.modules.match_official_report.controller;

import com.example.golbackend.modules.match_official_report.dto.MatchOfficialReportDto;
import com.example.golbackend.modules.match_official_report.dto.MatchOfficialReportResponseDto;
import com.example.golbackend.modules.match_official_report.services.MatchOfficialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches/{matchId}/official-reports")
public class MatchOfficialReportController {

    @Autowired
    private MatchOfficialReportService reportService;

    @PostMapping
    public ResponseEntity<?> createReport(@PathVariable Long matchId,
                                          @RequestBody MatchOfficialReportDto reportDto) {
        try {
            MatchOfficialReportResponseDto created = reportService.createReport(matchId, reportDto);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<MatchOfficialReportResponseDto>> getReports(@PathVariable Long matchId) {
        return ResponseEntity.ok(reportService.getReportsByMatch(matchId));
    }
}
