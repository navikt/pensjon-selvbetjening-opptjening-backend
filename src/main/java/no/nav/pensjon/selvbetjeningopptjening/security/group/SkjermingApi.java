package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;

public interface SkjermingApi {

    boolean isSkjermet(Pid pid);
}
