package no.nav.pensjon.selvbetjeningopptjening.mock;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.*;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.dto.AktoerDto;

import java.time.LocalDate;

import static java.util.Collections.emptyList;

public class FullmaktBuilder {

    public static final LocalDate TODAY = LocalDate.of(2021, 6, 15);
    public static final String FULLMAKTSGIVER_PID = "04925398980";
    public static final String FULLMEKTIG_PID = "30915399246";
    private boolean gyldig = true;
    private int fomMonth = 1;
    private int tomMonth = 12;
    private Fagomraade fagomraade = Fagomraade.PEN;
    private Fullmaktnivaa nivaa = Fullmaktnivaa.FULLSTENDIG;
    private Aktoertype fullmaktsgiverType = Aktoertype.PERSON;
    private Aktoertype fullmektigType = Aktoertype.PERSON;

    public FullmaktBuilder withStatusNotGyldig() {
        gyldig = false;
        return this;
    }

    public FullmaktBuilder withStartInFuture() {
        fomMonth = TODAY.getMonthValue() + 1;
        return this;
    }

    public FullmaktBuilder withEndInPast() {
        tomMonth = TODAY.getMonthValue() - 1;
        return this;
    }

    public FullmaktBuilder withNoFagomraade() {
        fagomraade = Fagomraade.NONE;
        return this;
    }

    public FullmaktBuilder withNonPersonFullmaktsgiver() {
        fullmaktsgiverType = Aktoertype.NONE;
        return this;
    }

    public FullmaktBuilder withNonPersonFullmektig() {
        fullmektigType = Aktoertype.NONE;
        return this;
    }

    public Fullmakt build() {
        return new Fullmakt(
                1L,
                Fullmakttype.SELVBET,
                nivaa,
                LocalDate.of(2021, fomMonth, 1),
                LocalDate.of(2021, tomMonth, 28),
                gyldig,
                2,
                fagomraade,
                aktoer(FULLMAKTSGIVER_PID, fullmaktsgiverType),
                aktoer(FULLMEKTIG_PID, fullmektigType),
                "oppretter",
                LocalDate.of(2020, 11, 11),
                "endrer",
                LocalDate.of(2020, 12, 12));
    }

    private static AktoerDto aktoer(String pid, Aktoertype type) {
        return new AktoerDto(pid, type.name(), emptyList(), emptyList());
    }
}
