package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfoGetter;

import java.util.List;
import java.util.Map;

public class ByProfileStrategy extends BeanStrategy {

    private static final int TIPPING_POINT = 1962;

    @Override
    public String getName() {
        return "byProfile";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        if (parameters == null) {
            return false;
        }

        String profile = parameters.get("profile");

        if (!("noUttakAndBornAfter" + TIPPING_POINT).equals(profile)) {
            return false;
        }

        Pid pid = getBean(LoginInfoGetter.class).getLoginInfo().getPid();
        return uttaksgrader(pid.getPid()).isEmpty() && pid.getFodselsdato().getYear() > TIPPING_POINT;
    }

    private List<Uttaksgrad> uttaksgrader(String fnr) {
        return getBean(UttaksgradGetter.class)
                .getAlderSakUttaksgradhistorikkForPerson(fnr);
    }
}
