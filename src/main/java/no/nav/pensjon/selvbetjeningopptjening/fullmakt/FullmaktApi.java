package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import java.util.List;

public interface FullmaktApi {

    boolean harFullmaktsforhold(String fullmaktsgiverPid, String fullmektigPid);
}
