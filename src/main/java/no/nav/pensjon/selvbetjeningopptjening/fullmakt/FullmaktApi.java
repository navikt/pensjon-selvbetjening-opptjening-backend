package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;

public interface FullmaktApi {

    RepresentasjonValidity fetchRepresentasjonsgyldighet(String fullmaktsgiverPid);
}
