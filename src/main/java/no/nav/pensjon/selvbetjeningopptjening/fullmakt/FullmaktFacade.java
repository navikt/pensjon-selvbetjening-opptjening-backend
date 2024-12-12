package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
import org.springframework.stereotype.Component;

@Component
public class FullmaktFacade {

    private final FullmaktApi fullmaktService;

    public FullmaktFacade(FullmaktApi fullmaktService) {
        this.fullmaktService = fullmaktService;
    }

    public RepresentasjonValidity mayActOnBehalfOf(String fullmaktsgiverPid) {
        return fullmaktService.fetchRepresentasjonsgyldighet(fullmaktsgiverPid);
    }
}
