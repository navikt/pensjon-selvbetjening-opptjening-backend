package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import org.glassfish.jersey.internal.util.Producer;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public class AfpHistorikk {

    private final LocalDate virkningFomDate;
    private final LocalDate virkningTomDate;
    private final boolean hasVirkningTomDate;

    public AfpHistorikk(LocalDate virkningFomDate, LocalDate virkningTomDate) {
        this.virkningFomDate = requireNonNull(virkningFomDate);
        this.virkningTomDate = virkningTomDate;
        this.hasVirkningTomDate = virkningTomDate != null;
    }

    public LocalDate getVirkningFomDate() {
        return virkningFomDate;
    }

    public int getStartYear() {
        return virkningFomDate.getYear();
    }

    public int getEndYearOrDefault(Producer<Integer> defaultYear) {
        return hasVirkningTomDate ? virkningTomDate.getYear() : defaultYear.call();
    }
}
