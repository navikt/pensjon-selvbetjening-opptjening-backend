package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import java.util.List;

public interface FullmaktApi {

    List<Fullmakt> getFullmakter(String fullmaktsgiverPid); // throws ConsumerException;
}
