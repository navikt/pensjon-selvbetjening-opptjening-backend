package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public class Uforeperiode {

    private final int uforegrad;
    private UforeTypeCode uforetype;
    private final LocalDate fomDate;
    private final LocalDate tomDate;
    private final boolean hasTomDate;

    public Uforeperiode(Integer uforegrad, UforeTypeCode uforetype, LocalDate fomDate, LocalDate tomDate) {
        this.uforegrad = uforegrad == null ? 0 : uforegrad;
        this.uforetype = uforetype;
        this.fomDate = fomDate;
        this.tomDate = tomDate;
        this.hasTomDate = tomDate != null;
    }

    public int getUforegrad() {
        return uforegrad;
    }

    public LocalDate getFomDate() {
        return fomDate;
    }

    public LocalDate getTomDate() {
        return tomDate;
    }

    public boolean hasTomDate() {
        return hasTomDate;
    }

    public UforeTypeCode getUforetype() {
        return uforetype;
    }

    boolean isReal() {
        return UforeTypeCode.UF_M_YRKE.equals(uforetype) ||
                UforeTypeCode.UFORE.equals(uforetype) ||
                UforeTypeCode.YRKE.equals(uforetype);
    }

    boolean isStrictReal() {
        return UforeTypeCode.UFORE.equals(uforetype) ||
                UforeTypeCode.UF_M_YRKE.equals(uforetype);
    }

    boolean isVirkFomBeforeGrunnlagYear(int year) {
        LocalDate firstDayInYear = LocalDate.of(year, 1, 1);

        return fomDate.getYear() == year ||
                isDateBeforeOrEqual(fomDate, firstDayInYear) &&
                        (!hasTomDate || isSameYearOrDateBeforeOrEqual(firstDayInYear, tomDate));
    }

    private static boolean isSameYearOrDateBeforeOrEqual(LocalDate x, LocalDate y) {
        return x.getYear() == y.getYear() || isDateBeforeOrEqual(x, y);
    }

    private static boolean isDateBeforeOrEqual(LocalDate x, LocalDate y) {
        return x.isBefore(y) || x.isEqual(y);
    }
}
