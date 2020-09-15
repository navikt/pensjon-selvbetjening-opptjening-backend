package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import java.util.List;
import java.util.Map;

import no.finn.unleash.strategy.Strategy;

import no.nav.pensjon.selvbetjeningopptjening.config.SpringContext;
import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradConsumer;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.util.FnrUtil;

public class ByProfileStrategy implements Strategy {
    @Override
    public String getName() {
        return "byProfile";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        if (parameters != null) {
            String profile = parameters.get("profile");
            if ("noUttakAndBornAfter1962".equals(profile)) {
                String fnr = SpringContext.getBean(StringExtractor.class).extract();
                List<Uttaksgrad> uttaksgradList = SpringContext.getBean(UttaksgradConsumer.class).getAlderSakUttaksgradhistorikkForPerson(fnr);
                return uttaksgradList.isEmpty() && FnrUtil.getFodselsdatoForFnr(fnr).getYear() > 1962;
            }
        }
        return false;
    }
}
