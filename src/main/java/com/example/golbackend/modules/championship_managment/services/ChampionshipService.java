package com.example.golbackend.modules.championship_managment.services;

import com.example.golbackend.modules.championship_managment.model.ChampionshipUpdateRequest;
import com.example.golbackend.modules.championship_managment.exception.ChampionshipNotFoundException;
import com.example.golbackend.modules.championship_managment.model.Championship;
import com.example.golbackend.modules.championship_managment.repositories.ChampionshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChampionshipService {

    private final ChampionshipRepository championshipRepository;

    public Championship createChampionship(Championship championship) {
        return championshipRepository.save(championship);
    }

    public List<Championship> listChampionship() {
        return championshipRepository.findAll();
    }

    public Championship getChampionshipById(Long id) {
        return championshipRepository.findById(id)
                .orElseThrow(() -> new ChampionshipNotFoundException(id));
    }

    public Championship updateChampionship(Long id, ChampionshipUpdateRequest req) {
        Championship championship = championshipRepository.findById(id)
                .orElseThrow(() -> new ChampionshipNotFoundException(id));

        if (req.getChampionshipName() != null) championship.setChampionshipName(req.getChampionshipName());
        if (req.getChampionshipDescription() != null) championship.setChampionshipDescription(req.getChampionshipDescription());
        if (req.getStartDate() != null) championship.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) championship.setEndDate(req.getEndDate());
        if (req.getChampionshipStatus() != null) championship.setChampionshipStatus(req.getChampionshipStatus());
        if (req.getMinPlayersField() != null) championship.setMinPlayersField(req.getMinPlayersField());
        if (req.getMaxPlayersField() != null) championship.setMaxPlayersField(req.getMaxPlayersField());
        if (req.getMinPlayersRegistered() != null) championship.setMinPlayersRegistered(req.getMinPlayersRegistered());
        if (req.getMaxPlayersRegistered() != null) championship.setMaxPlayersRegistered(req.getMaxPlayersRegistered());
        if (req.getYellowDoubleSuspensionMatches() != null) championship.setYellowDoubleSuspensionMatches(req.getYellowDoubleSuspensionMatches());
        if (req.getRedSuspensionMatches() != null) championship.setRedSuspensionMatches(req.getRedSuspensionMatches());
        if (req.getYellowAccumulationSuspensionMatches() != null) championship.setYellowAccumulationSuspensionMatches(req.getYellowAccumulationSuspensionMatches());
        if (req.getYellowAccumulationNumber() != null) championship.setYellowAccumulationNumber(req.getYellowAccumulationNumber());
        if (req.getPointsWin() != null) championship.setPointsWin(req.getPointsWin());
        if (req.getPointsLose() != null) championship.setPointsLose(req.getPointsLose());
        if (req.getPointsDraw() != null) championship.setPointsDraw(req.getPointsDraw());
        if (req.getMaxSubstitutions() != null) championship.setMaxSubstitutions(req.getMaxSubstitutions());
        if (req.getReentryAllowed() != null) championship.setReentryAllowed(req.getReentryAllowed());
        if (req.getForfeitGoalsFor() != null) championship.setForfeitGoalsFor(req.getForfeitGoalsFor());
        if (req.getForfeitGoalsAgainst() != null) championship.setForfeitGoalsAgainst(req.getForfeitGoalsAgainst());
        if (req.getNoShowFineAmount() != null) championship.setNoShowFineAmount(req.getNoShowFineAmount());
        if (req.getResetCardsGroup() != null) championship.setResetCardsGroup(req.getResetCardsGroup());
        if (req.getResetCardsQuarterfinal() != null) championship.setResetCardsQuarterfinal(req.getResetCardsQuarterfinal());
        if (req.getResetCardsSemifinal() != null) championship.setResetCardsSemifinal(req.getResetCardsSemifinal());
        if (req.getResetCardsFinal() != null) championship.setResetCardsFinal(req.getResetCardsFinal());

        return championshipRepository.save(championship);
    }

    public void deleteChampionship(Long id) {
        Championship championship = championshipRepository.findById(id)
                .orElseThrow(() -> new ChampionshipNotFoundException(id));
        championshipRepository.delete(championship);
    }
}
