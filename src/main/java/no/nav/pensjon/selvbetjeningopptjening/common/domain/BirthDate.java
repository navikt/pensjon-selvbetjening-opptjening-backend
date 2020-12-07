package no.nav.pensjon.selvbetjeningopptjening.common.domain;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public class BirthDate {

    private final LocalDate value;
    private final boolean basedOnYearOnly;

    public BirthDate(LocalDate value) {
        this.value = requireNonNull(value);
        this.basedOnYearOnly = false;
    }

    public BirthDate(Integer year) {
        this.value = LocalDate.of(requireNonNull(year), 1, 1);
        this.basedOnYearOnly = true;
    }

    public LocalDate getValue() {
        return value;
    }

    public boolean isBasedOnYearOnly() {
        return basedOnYearOnly;
    }
}
