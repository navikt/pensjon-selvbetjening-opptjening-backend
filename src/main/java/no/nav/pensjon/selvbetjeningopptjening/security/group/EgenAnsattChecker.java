package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class EgenAnsattChecker {

    private final SkjermingApi skjermingApi;

    public EgenAnsattChecker(SkjermingApi skjermingApi) {
        this.skjermingApi = requireNonNull(skjermingApi);
    }

    public boolean isEgenAnsatt(Pid pid) {
        return skjermingApi.isEgenAnsatt(pid);
    }
}
