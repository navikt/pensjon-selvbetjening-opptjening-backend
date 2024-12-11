package no.nav.pensjon.selvbetjeningopptjening.fullmakt;


import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;

public interface FullmaktApi {

    boolean harFullmaktsforhold(String fullmaktsgiverPid, String fullmektigPid);

    RepresentasjonValidity hasValidRepresentasjonsforhold(String fullmaktsgiverPid, String fullmektigPid);
}
