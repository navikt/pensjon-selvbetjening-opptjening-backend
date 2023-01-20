package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public class Fullmakt {

    private final long id;
    private final Fullmakttype type;
    private final Fullmaktnivaa nivaa;
    private final LocalDate fom;
    private final LocalDate tom;
    private final boolean gyldig;
    private final int versjon;
    private final Fagomraade fagomraade;
    private final Aktoer giver;
    private final Aktoer fullmektig;
    private final boolean lastsForever;

    public Fullmakt(long id,
                    Fullmakttype type,
                    Fullmaktnivaa nivaa,
                    LocalDate fom,
                    LocalDate tom,
                    boolean gyldig,
                    int versjon,
                    Fagomraade fagomraade,
                    Aktoer giver,
                    Aktoer fullmektig) {
        this.id = id;
        this.type = type == null ? Fullmakttype.NONE : type;
        this.nivaa = nivaa == null ? Fullmaktnivaa.NONE : nivaa;
        this.fom = requireNonNull(fom, "fom");
        this.lastsForever = tom == null;
        this.tom = lastsForever ? LocalDate.MAX : tom;
        this.gyldig = gyldig;
        this.versjon = versjon;
        this.fagomraade = fagomraade == null ? Fagomraade.NONE : fagomraade;
        this.giver = requireNonNull(giver, "giver");
        this.fullmektig = requireNonNull(fullmektig, "fullmektig");
    }

    public long getId() {
        return id;
    }

    public Fullmakttype getType() {
        return type;
    }

    public Fullmaktnivaa getNivaa() {
        return nivaa;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public boolean lastsForever() {
        return lastsForever;
    }

    public boolean isGyldig() {
        return gyldig;
    }

    public int getVersjon() {
        return versjon;
    }

    public Fagomraade getFagomrade() {
        return fagomraade;
    }

    public Aktoer getGiver() {
        return giver;
    }

    public Aktoer getFullmektig() {
        return fullmektig;
    }

    boolean isValidFor(String giverPid, String fullmektigPid, LocalDate date) {
        return gyldig
                && fagomraade.validForPensjon()
                && hasRequiredNivaa()
                && !isInFuture(date) && !isExpired(date)
                && giver.isPerson(giverPid)
                && fullmektig.isPerson(fullmektigPid);
    }

    private boolean hasRequiredNivaa() {
        return Fullmaktnivaa.FULLSTENDIG.equals(nivaa)
                || Fullmaktnivaa.BEGRENSET.equals(nivaa)
                || Fullmaktnivaa.SAMORDPLIK.equals(nivaa);
    }

    private boolean isInFuture(LocalDate date) {
        return date.isBefore(fom);
    }

    private boolean isExpired(LocalDate date) {
        return !lastsForever && tom.isBefore(date);
    }
}
