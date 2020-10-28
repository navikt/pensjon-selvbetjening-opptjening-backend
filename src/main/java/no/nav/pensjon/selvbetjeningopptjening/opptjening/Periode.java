package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.time.LocalDate;

public interface Periode {

    LocalDate getFomDato();

    LocalDate getTomDato();
}
