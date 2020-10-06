package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

import java.util.List;

public interface UttaksgradGetter {

    List<Uttaksgrad> getUttaksgradForVedtak(List<Long> vedtakIdList);

    List<Uttaksgrad> getAlderSakUttaksgradhistorikkForPerson(String fnr);
}
