package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
import org.springframework.stereotype.Component;

@Component
public class FullmaktService implements FullmaktApi {

    private final FullmaktClient client;

    public FullmaktService(FullmaktClient client) {
        this.client = client;
    }

    @Override
    public boolean harFullmaktsforhold(String fullmaktsgiverPid) {
        RepresentasjonValidity response = client.hasValidRepresentasjonsforhold(fullmaktsgiverPid);
        if (response == null || response.hasValidRepresentasjonsforhold() == null) {
            return false;
        }
        return response.hasValidRepresentasjonsforhold();
    }
}
