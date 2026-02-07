package com.example.golbackend.modules.match.services;

import com.example.golbackend.modules.championship_managment.model.Championship;
import com.example.golbackend.modules.championship_managment.repositories.ChampionshipRepository;
import com.example.golbackend.modules.match.dto.*;
import com.example.golbackend.modules.match.model.Match;
import com.example.golbackend.modules.match.repositories.MatchRepository;
import com.example.golbackend.modules.matchday.model.Matchday;
import com.example.golbackend.modules.matchday.repositories.MatchdayRepository;
import com.example.golbackend.modules.phase.model.Phase;
import com.example.golbackend.modules.standing.model.Standing;
import com.example.golbackend.modules.standing.repositories.StandingRepository;
import com.example.golbackend.modules.team_managment.model.Team;
import com.example.golbackend.modules.team_managment.repositories.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Autowired private MatchRepository matchRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private StandingRepository standingRepository;
    @Autowired private ChampionshipRepository championshipRepository;
    @Autowired private com.example.golbackend.modules.phase.repositories.PhaseRepository phaseRepository;

    public Match createMatch(MatchDto dto) {

        Phase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new RuntimeException("Phase not found with id: " + dto.getPhaseId()));

        Championship championship = phase.getChampionship();
        if (dto.getChampionshipId() != null) {
            championship = championshipRepository.findById(dto.getChampionshipId())
                    .orElseThrow(() -> new RuntimeException("Championship not found with id: " + dto.getChampionshipId()));
        }

        Team home = null;
        Team away = null;

        if (dto.getHomeTeamId() != null) {
            home = teamRepository.findById(dto.getHomeTeamId())
                    .orElseThrow(() -> new RuntimeException("Home Team not found with id: " + dto.getHomeTeamId()));
        }
        if (dto.getAwayTeamId() != null) {
            away = teamRepository.findById(dto.getAwayTeamId())
                    .orElseThrow(() -> new RuntimeException("Away Team not found with id: " + dto.getAwayTeamId()));
        }

        Match match = new Match();
        match.setPhase(phase);
        match.setChampionship(championship);

        match.setHomeTeam(home);
        match.setAwayTeam(away);

        match.setMatchDate(dto.getMatchDate());
        match.setFieldName(dto.getFieldName());
        match.setRefereeName(dto.getRefereeName());

        match.setRoundNumber(dto.getRoundNumber());
        match.setGroupIdentifier(dto.getGroupIdentifier());
        match.setBracketCode(dto.getBracketCode());
        match.setLeg(dto.getLeg() != null ? dto.getLeg() : 1);
        match.setMatchdayNumber(dto.getMatchdayNumber() != null ? dto.getMatchdayNumber() : 1);


        match.setStatus("SCHEDULED");

        if (dto.getWinnerGoesToMatchId() != null) {
            Match next = matchRepository.findById(dto.getWinnerGoesToMatchId())
                    .orElseThrow(() -> new RuntimeException("Winner next match not found: " + dto.getWinnerGoesToMatchId()));
            match.setWinnerGoesToMatch(next);
        }
        if (dto.getLoserGoesToMatchId() != null) {
            Match next = matchRepository.findById(dto.getLoserGoesToMatchId())
                    .orElseThrow(() -> new RuntimeException("Loser next match not found: " + dto.getLoserGoesToMatchId()));
            match.setLoserGoesToMatch(next);
        }

        return matchRepository.save(match);
    }

    @Transactional
    public List<MatchResponseDto> createMatchesBulk(BulkCreateMatchesDto bulk) {

        if (bulk.getMatches() == null || bulk.getMatches().isEmpty()) {
            throw new RuntimeException("matches list is required and cannot be empty");
        }

        Long defaultPhaseId = bulk.getPhaseId();
        Long defaultChampId = bulk.getChampionshipId();

        for (int i = 0; i < bulk.getMatches().size(); i++) {
            MatchDto m = bulk.getMatches().get(i);

            Long effectivePhaseId = (m.getPhaseId() != null) ? m.getPhaseId() : defaultPhaseId;
            if (effectivePhaseId == null) {
                throw new RuntimeException("Item #" + i + " missing phaseId and no default phaseId provided");
            }

            boolean hasAnyTeam = (m.getHomeTeamId() != null || m.getAwayTeamId() != null);
            if (hasAnyTeam) {
                if (m.getHomeTeamId() == null || m.getAwayTeamId() == null) {
                    throw new RuntimeException("Item #" + i + " must have both homeTeamId and awayTeamId (or both null for placeholder)");
                }
                if (Objects.equals(m.getHomeTeamId(), m.getAwayTeamId())) {
                    throw new RuntimeException("Item #" + i + " homeTeamId cannot equal awayTeamId");
                }
            }


        }

        Set<Long> phaseIds = bulk.getMatches().stream()
                .map(m -> m.getPhaseId() != null ? m.getPhaseId() : defaultPhaseId)
                .collect(Collectors.toSet());

        Map<Long, Phase> phaseMap = new HashMap<>();
        for (Long pid : phaseIds) {
            Phase phase = phaseRepository.findById(pid)
                    .orElseThrow(() -> new RuntimeException("Phase not found with id: " + pid));
            phaseMap.put(pid, phase);
        }

        Set<Long> teamIds = bulk.getMatches().stream()
                .flatMap(m -> Arrays.stream(new Long[]{m.getHomeTeamId(), m.getAwayTeamId()}))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Team> teamMap = new HashMap<>();
        if (!teamIds.isEmpty()) {
            List<Team> teams = teamRepository.findAllById(teamIds);
            teamMap = teams.stream().collect(Collectors.toMap(Team::getId, t -> t));

            for (Long tid : teamIds) {
                if (!teamMap.containsKey(tid)) {
                    throw new RuntimeException("Team not found with id: " + tid);
                }
            }
        }

        List<Match> toSave = new ArrayList<>();

        for (int i = 0; i < bulk.getMatches().size(); i++) {
            MatchDto dto = bulk.getMatches().get(i);

            Long phaseId = (dto.getPhaseId() != null) ? dto.getPhaseId() : defaultPhaseId;
            Phase phase = phaseMap.get(phaseId);

            var championship = phase.getChampionship();
            if (defaultChampId != null || dto.getChampionshipId() != null) {
                Long champId = dto.getChampionshipId() != null ? dto.getChampionshipId() : defaultChampId;
                championship = championshipRepository.findById(champId)
                        .orElseThrow(() -> new RuntimeException("Championship not found with id: " + champId));
            }

            Match match = new Match();
            match.setPhase(phase);
            match.setChampionship(championship);

            if (dto.getHomeTeamId() != null) match.setHomeTeam(teamMap.get(dto.getHomeTeamId()));
            if (dto.getAwayTeamId() != null) match.setAwayTeam(teamMap.get(dto.getAwayTeamId()));

            match.setMatchDate(dto.getMatchDate());
            match.setFieldName(dto.getFieldName());
            match.setRefereeName(dto.getRefereeName());

            match.setRoundNumber(dto.getRoundNumber());
            match.setGroupIdentifier(dto.getGroupIdentifier());
            match.setBracketCode(dto.getBracketCode());
            match.setLeg(dto.getLeg() != null ? dto.getLeg() : 1);
            match.setMatchdayNumber(dto.getMatchdayNumber() != null ? dto.getMatchdayNumber() : 1);


            match.setStatus("SCHEDULED");

            if (dto.getWinnerGoesToMatchId() != null) {
                Match next = matchRepository.findById(dto.getWinnerGoesToMatchId())
                        .orElseThrow(() -> new RuntimeException("Winner next match not found: " + dto.getWinnerGoesToMatchId()));
                match.setWinnerGoesToMatch(next);
            }
            if (dto.getLoserGoesToMatchId() != null) {
                Match next = matchRepository.findById(dto.getLoserGoesToMatchId())
                        .orElseThrow(() -> new RuntimeException("Loser next match not found: " + dto.getLoserGoesToMatchId()));
                match.setLoserGoesToMatch(next);
            }

            toSave.add(match);
        }

        List<Match> saved = matchRepository.saveAll(toSave);

        return saved.stream().map(this::toDto).toList();
    }

    public List<Match> getMatchesByPhase(Long phaseId) {
        return matchRepository.findByPhasePhaseId(phaseId);
    }

    @Transactional
    public Match updateMatchResult(Long matchId, UpdateMatchResultDto dto) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        if ("FINISHED".equals(match.getStatus()) && match.getHomeScore() != null && match.getAwayScore() != null) {
            updateStandings(match, true);
        }

        match.setHomeScore(dto.getHomeScore());
        match.setAwayScore(dto.getAwayScore());
        match.setHomePenalties(dto.getHomePenalties());
        match.setAwayPenalties(dto.getAwayPenalties());
        match.setStatus(dto.getStatus());

        Match saved = matchRepository.save(match);

        if ("FINISHED".equals(saved.getStatus()) && saved.getHomeScore() != null && saved.getAwayScore() != null) {
            updateStandings(saved, false);
        }

        return saved;
    }

    private void updateStandings(Match match, boolean revert) {

        Phase phase = match.getPhase();
        Long phaseId = phase.getPhaseId();
        Championship championship = (match.getChampionship() != null) ? match.getChampionship() : phase.getChampionship();

        if (match.getHomeTeam() == null || match.getAwayTeam() == null) return;

        Standing homeStanding = standingRepository.findByPhasePhaseIdAndTeam_Id(phaseId, match.getHomeTeam().getId())
                .orElseGet(() -> createInitialStanding(phase, match.getHomeTeam()));

        Standing awayStanding = standingRepository.findByPhasePhaseIdAndTeam_Id(phaseId, match.getAwayTeam().getId())
                .orElseGet(() -> createInitialStanding(phase, match.getAwayTeam()));

        int multiplier = revert ? -1 : 1;

        int home = match.getHomeScore();
        int away = match.getAwayScore();

        homeStanding.setMatchesPlayed(homeStanding.getMatchesPlayed() + multiplier);
        awayStanding.setMatchesPlayed(awayStanding.getMatchesPlayed() + multiplier);

        homeStanding.setGoalsFor(homeStanding.getGoalsFor() + (home * multiplier));
        homeStanding.setGoalsAgainst(homeStanding.getGoalsAgainst() + (away * multiplier));
        awayStanding.setGoalsFor(awayStanding.getGoalsFor() + (away * multiplier));
        awayStanding.setGoalsAgainst(awayStanding.getGoalsAgainst() + (home * multiplier));

        if (home > away) {
            homeStanding.setWins(homeStanding.getWins() + multiplier);
            homeStanding.setPoints(homeStanding.getPoints() + (championship.getPointsWin() * multiplier));

            awayStanding.setLosses(awayStanding.getLosses() + multiplier);
            awayStanding.setPoints(awayStanding.getPoints() + (championship.getPointsLose() * multiplier));
        } else if (home < away) {
            homeStanding.setLosses(homeStanding.getLosses() + multiplier);
            homeStanding.setPoints(homeStanding.getPoints() + (championship.getPointsLose() * multiplier));

            awayStanding.setWins(awayStanding.getWins() + multiplier);
            awayStanding.setPoints(awayStanding.getPoints() + (championship.getPointsWin() * multiplier));
        } else {
            homeStanding.setDraws(homeStanding.getDraws() + multiplier);
            homeStanding.setPoints(homeStanding.getPoints() + (championship.getPointsDraw() * multiplier));

            awayStanding.setDraws(awayStanding.getDraws() + multiplier);
            awayStanding.setPoints(awayStanding.getPoints() + (championship.getPointsDraw() * multiplier));
        }

        homeStanding.setGoalDifference(homeStanding.getGoalsFor() - homeStanding.getGoalsAgainst());
        awayStanding.setGoalDifference(awayStanding.getGoalsFor() - awayStanding.getGoalsAgainst());

        standingRepository.save(homeStanding);
        standingRepository.save(awayStanding);
    }

    private Standing createInitialStanding(Phase phase, Team team) {
        Standing standing = new Standing();
        standing.setPhase(phase);
        standing.setTeam(team);
        return standing;
    }

    public MatchResponseDto toDto(Match m) {
        return MatchResponseDto.builder()
                .matchId(m.getMatchId())
                .championshipId(m.getChampionship() != null
                        ? m.getChampionship().getChampionshipId()
                        : null)
                .phaseId(m.getPhase() != null
                        ? m.getPhase().getPhaseId()
                        : null)
                .homeTeamId(m.getHomeTeam() != null
                        ? m.getHomeTeam().getId()
                        : null)
                .awayTeamId(m.getAwayTeam() != null
                        ? m.getAwayTeam().getId()
                        : null)
                .homeScore(m.getHomeScore())
                .awayScore(m.getAwayScore())
                .homePenalties(m.getHomePenalties())
                .awayPenalties(m.getAwayPenalties())
                .status(m.getStatus())
                .matchDate(m.getMatchDate())
                .fieldName(m.getFieldName())
                .matchdayNumber(m.getMatchdayNumber())
                .refereeName(m.getRefereeName())
                .roundNumber(m.getRoundNumber())
                .groupIdentifier(m.getGroupIdentifier())
                .bracketCode(m.getBracketCode())
                .leg(m.getLeg())
                .winnerGoesToMatchId(
                        m.getWinnerGoesToMatch() != null
                                ? m.getWinnerGoesToMatch().getMatchId()
                                : null)
                .loserGoesToMatchId(
                        m.getLoserGoesToMatch() != null
                                ? m.getLoserGoesToMatch().getMatchId()
                                : null)
                .build();
    }


    public List<MatchResponseDto> getMatchesByPhaseAndMatchday(Long phaseId, Integer matchdayNumber) {
        return matchRepository.findByPhasePhaseIdAndMatchdayNumber(phaseId, matchdayNumber)
                .stream().map(this::toDto).toList();
    }

    public List<MatchResponseDto> getMatchesByDateRange(LocalDate from, LocalDate to) {

        if (from == null || to == null) {
            throw new RuntimeException("from and to are required");
        }

        if (to.isBefore(from)) {
            throw new RuntimeException("to cannot be before from");
        }

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        return matchRepository
                .findByMatchDateBetweenOrderByMatchDateAsc(fromDateTime, toDateTime)
                .stream()
                .map(this::toDto)
                .toList();
    }


    @Transactional
    public Match rescheduleMatch(Long matchId, RescheduleMatchDto dto) {

        if (dto == null || dto.getMatchDate() == null) {
            throw new RuntimeException("matchDate is required");
        }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        if ("FINISHED".equals(match.getStatus())) {
            throw new RuntimeException("Cannot reschedule a FINISHED match");
        }

        match.setMatchDate(dto.getMatchDate());

        if (dto.getFieldName() != null) {
            match.setFieldName(dto.getFieldName());
        }
        if (dto.getRefereeName() != null) {
            match.setRefereeName(dto.getRefereeName());
        }

        return matchRepository.save(match);
    }


}
