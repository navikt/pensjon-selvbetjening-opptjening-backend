package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

import java.util.List;
import java.util.Map;

import static no.nav.pensjon.selvbetjeningopptjening.util.FnrUtil.getFodselsdatoForFnr;

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

        String fnr = getBean(StringExtractor.class).extract();
        return uttaksgrader(fnr).isEmpty() && getFodselsdatoForFnr(fnr).getYear() > TIPPING_POINT;
    }

    private List<Uttaksgrad> uttaksgrader(String fnr) {
        return getBean(UttaksgradGetter.class)
                .getAlderSakUttaksgradhistorikkForPerson(fnr);
    }
}
