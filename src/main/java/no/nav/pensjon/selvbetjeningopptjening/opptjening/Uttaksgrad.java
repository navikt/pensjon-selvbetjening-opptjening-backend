package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.time.LocalDate;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Uttaksgrad {

    private final long vedtakId;
    private final int uttaksgrad;
    private final LocalDate fomDate;
    private final LocalDate tomDate;
    private final boolean hasTomDate;

    public Uttaksgrad(Long vedtakId, Integer uttaksgrad, LocalDate fomDate, LocalDate tomDate) {
        this.vedtakId = vedtakId == null ? 0 : vedtakId;
        this.uttaksgrad = uttaksgrad == null ? 0 : uttaksgrad;
        this.fomDate = requireNonNull(fomDate);
        this.tomDate = tomDate;
        this.hasTomDate = tomDate != null;
    }

    public long getVedtakId() {
        return vedtakId;
    }

    public int getUttaksgrad() {
        return uttaksgrad;
    }

    public LocalDate getFomDate() {
        return fomDate;
    }

     LocalDate getTomDate() {
        return tomDate;
    }

    boolean coversYear(int year) {
        return fomDate.getYear() <= year && endsAtOrAfter(year);
    }

   private boolean endsAtOrAfter(int year) {
        return !hasTomDate || year <= tomDate.getYear();
    }

    boolean isGradert() {
        return 0 < uttaksgrad && uttaksgrad < 100;
    }
}
