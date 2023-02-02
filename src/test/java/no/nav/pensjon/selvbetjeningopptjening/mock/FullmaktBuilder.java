package no.nav.pensjon.selvbetjeningopptjening.mock;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.*;

import java.time.LocalDate;

import static java.util.Collections.emptyList;

public class FullmaktBuilder {

    public static final LocalDate TODAY = LocalDate.of(2021, 6, 15);
    private boolean gyldig = true;
    private int fomMonth = 1;
    private int tomMonth = 12;
    private int versjon = 1;
    private Fagomraade fagomraade = Fagomraade.PEN;
    private Fullmaktnivaa nivaa = Fullmaktnivaa.FULLSTENDIG;
    private Aktoertype fullmaktsgiverType = Aktoertype.PERSON;
    private Aktoertype fullmektigType = Aktoertype.PERSON;
    private String fullmaktsgiverPid = "04925398980";
    private String fullmektigPid = "30915399246";

    public FullmaktBuilder withStatusGyldig() {
        gyldig = true;
        return this;
    }

    public FullmaktBuilder withStatusNotGyldig() {
        gyldig = false;
        return this;
    }

    public FullmaktBuilder withStartInPast() {
        fomMonth = TODAY.getMonthValue() - 1;
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

    public FullmaktBuilder withEndInFuture() {
        tomMonth = TODAY.getMonthValue() + 1;
        return this;
    }

    public FullmaktBuilder withNoEnd() {
        tomMonth = 0;
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
    public FullmaktBuilder withSamhandlerFullmaktsgiver() {
        fullmaktsgiverType = Aktoertype.SAMHANDLER;
        return this;
    }

    public FullmaktBuilder withNonPersonFullmektig() {
        fullmektigType = Aktoertype.NONE;
        return this;
    }

    public FullmaktBuilder withFagomraade(Fagomraade value) {
        fagomraade = value;
        return this;
    }

    public FullmaktBuilder withFullmaktsgiverPid(String value) {
        fullmaktsgiverPid = value;
        return this;
    }

    public FullmaktBuilder withFullmektigPid(String value) {
        fullmektigPid = value;
        return this;
    }

    public FullmaktBuilder withVersjon(int value) {
        versjon = value;
        return this;
    }

    public FullmaktBuilder withNivaa(Fullmaktnivaa value) {
        nivaa = value;
        return this;
    }

    public Fullmakt build() {
        return new Fullmakt(
                1L,
                Fullmakttype.SELVBET,
                nivaa,
                LocalDate.of(2021, fomMonth, 1),
                tomMonth == 0 ? null : LocalDate.of(2021, tomMonth, 28),
                gyldig,
                versjon,
                fagomraade,
                aktoer(fullmaktsgiverPid, fullmaktsgiverType),
                aktoer(fullmektigPid, fullmektigType));
    }

    private static Aktoer aktoer(String pid, Aktoertype type) {
        return new Aktoer(pid, type.name(), emptyList(), emptyList());
    }
}
