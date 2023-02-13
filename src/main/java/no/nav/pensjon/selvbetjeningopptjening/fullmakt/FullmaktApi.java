package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import java.util.List;

public interface FullmaktApi {

    List<Fullmakt> getFullmakter(String fullmaktsgiverPid);

    boolean harFullmaktsforhold(String fullmaktsgiverPid, String fullmektigPid);
}
