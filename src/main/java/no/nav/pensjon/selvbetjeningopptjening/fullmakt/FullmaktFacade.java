package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class FullmaktFacade {

    private final FullmaktApi fullmaktService;

    public FullmaktFacade(FullmaktApi fullmaktService) {
        this.fullmaktService = fullmaktService;
    }

    public boolean mayActOnBehalfOf(String fullmaktsgiverPid, String fullmektigPid) {
        if (fullmaktsgiverPid.equals(fullmektigPid)) {
            return true; // may act "on behalf of" oneself
        }
        return fullmaktService.harFullmaktsforhold(fullmaktsgiverPid, fullmektigPid);
    }

    public RepresentasjonValidity fetchRepresentasjonsgyldighet(String fullmaktsgiverPid, String fullmektigPid) {
        return fullmaktService.hasValidRepresentasjonsforhold(fullmaktsgiverPid, fullmektigPid);
    }
}
