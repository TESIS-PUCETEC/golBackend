package com.example.golbackend.modules.phase.services;

import com.example.golbackend.modules.championship_managment.model.Championship;
import com.example.golbackend.modules.championship_managment.repositories.ChampionshipRepository;
import com.example.golbackend.modules.phase.model.PhaseCreateRequest;
import com.example.golbackend.modules.phase.model.PhaseResponse;
import com.example.golbackend.modules.phase.model.PhaseMapper;
import com.example.golbackend.modules.phase.model.Phase;
import com.example.golbackend.modules.phase.repositories.PhaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class PhaseService {

    private final PhaseRepository phaseRepository;
    private final ChampionshipRepository championshipRepository;

    private static final Set<Integer> VALID_KNOCKOUT_START = Set.of(2, 4, 8, 16, 32, 64, 128);

    public PhaseService(PhaseRepository phaseRepository,
                        ChampionshipRepository championshipRepository) {
        this.phaseRepository = phaseRepository;
        this.championshipRepository = championshipRepository;
    }

    @Transactional
    public PhaseResponse createPhase(Long championshipId, PhaseCreateRequest req) {
        Championship championship = championshipRepository.findById(championshipId)
                .orElseThrow(() -> new RuntimeException("Championship not found with id: " + championshipId));

        // uq_phase_order (championship_id, phase_order)
        if (phaseRepository.existsByChampionshipChampionshipIdAndPhaseOrder(championshipId, req.getPhaseOrder())) {
            throw new IllegalArgumentException("phaseOrder " + req.getPhaseOrder()
                    + " already exists for championshipId " + championshipId);
        }

        // default status
        if (req.getStatus() == null) req.setStatus(Phase.PhaseStatus.PENDING);

        // valida + limpia campos
        normalizeAndValidate(req);

        Phase entity = PhaseMapper.toEntity(req, championship);
        Phase saved = phaseRepository.save(entity);

        return PhaseMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PhaseResponse> getPhasesByChampionship(Long championshipId) {
        return phaseRepository.findByChampionshipChampionshipIdOrderByPhaseOrderAsc(championshipId)
                .stream()
                .map(PhaseMapper::toResponse)
                .toList();
    }

    private void normalizeAndValidate(PhaseCreateRequest req) {
        if (req.getType() == null) throw new IllegalArgumentException("type is required");

        switch (req.getType()) {
            case GROUP -> {
                requireNonNull(req.getGroupsCount(), "groupsCount is required for GROUP");
                requireNonNull(req.getTeamsPerGroup(), "teamsPerGroup is required for GROUP");
                requireNonNull(req.getQualifiersPerGroup(), "qualifiersPerGroup is required for GROUP");
                requireNonNull(req.getBestThirdQualifiers(), "bestThirdQualifiers is required for GROUP");

                if (req.getCarryOverPoints() == null) req.setCarryOverPoints(false);

                req.setLeagueQualifiersCount(null);

                req.setKnockoutStartCount(null);
                req.setHasThirdPlaceMatch(null);
                req.setAwayGoalsRule(null);
                req.setPairingMethod(null);
                req.setMatchFormat(null);
            }

            case LEAGUE -> {
                requireNonNull(req.getLeagueQualifiersCount(), "leagueQualifiersCount is required for LEAGUE");

                req.setGroupsCount(null);
                req.setTeamsPerGroup(null);
                req.setQualifiersPerGroup(null);
                req.setBestThirdQualifiers(null);
                req.setCarryOverPoints(null);

                req.setKnockoutStartCount(null);
                req.setHasThirdPlaceMatch(null);
                req.setAwayGoalsRule(null);
                req.setPairingMethod(null);
                req.setMatchFormat(null);
            }

            case KNOCKOUT -> {
                requireNonNull(req.getKnockoutStartCount(), "knockoutStartCount is required for KNOCKOUT");
                requireNonNull(req.getPairingMethod(), "pairingMethod is required for KNOCKOUT");
                requireNonNull(req.getMatchFormat(), "matchFormat is required for KNOCKOUT");

                if (!VALID_KNOCKOUT_START.contains(req.getKnockoutStartCount())) {
                    throw new IllegalArgumentException("knockoutStartCount must be one of: 2,4,8,16,32,64,128");
                }

                if (req.getHasThirdPlaceMatch() == null) req.setHasThirdPlaceMatch(false);
                if (req.getAwayGoalsRule() == null) req.setAwayGoalsRule(false);

                req.setGroupsCount(null);
                req.setTeamsPerGroup(null);
                req.setQualifiersPerGroup(null);
                req.setBestThirdQualifiers(null);
                req.setCarryOverPoints(null);

                req.setLeagueQualifiersCount(null);
            }
        }
    }

    private void requireNonNull(Object value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
    }

    @Transactional
    public void deletePhase(Long championshipId, Long phaseId) {
        boolean exists = phaseRepository.existsByPhaseIdAndChampionshipChampionshipId(phaseId, championshipId);
        if (!exists) {
            throw new IllegalArgumentException(
                    "Phase not found with id: " + phaseId + " for championshipId: " + championshipId
            );
        }
        phaseRepository.deleteByPhaseIdAndChampionshipChampionshipId(phaseId, championshipId);
    }
}
