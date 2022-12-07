package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.dto.AktoerDto;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public class Fullmakt {

    private final long id;
    private final Fullmakttype type;
    private final Fullmaktnivaa niva;
    private final LocalDate fomDato;
    private final LocalDate tomDato;
    private final boolean gyldig;
    private final int versjon;
    private final Fagomraade fagomraade;
    private final AktoerDto giver;
    private final AktoerDto fullmektig;
    private final String opprettetAv; // fnr
    private final LocalDate opprettetDato;
    private final String endretAv; // fnr
    private final LocalDate endretDato;
    private final boolean lastsForever;

    public Fullmakt(long id,
                    Fullmakttype type,
                    Fullmaktnivaa niva,
                    LocalDate fomDato,
                    LocalDate tomDato,
                    boolean gyldig,
                    int versjon,
                    Fagomraade fagomraade,
                    AktoerDto giver,
                    AktoerDto fullmektig,
                    String opprettetAv,
                    LocalDate opprettetDato,
                    String endretAv,
                    LocalDate endretDato) {
        this.id = id;
        this.type = type == null ? Fullmakttype.NONE : type;
        this.niva = niva == null ? Fullmaktnivaa.NONE : niva;
        this.fomDato = requireNonNull(fomDato, "fomDato");
        this.lastsForever = tomDato == null;
        this.tomDato = lastsForever ? LocalDate.MAX : tomDato;
        this.gyldig = gyldig;
        this.versjon = versjon;
        this.fagomraade = fagomraade == null ? Fagomraade.NONE : fagomraade;
        this.giver = requireNonNull(giver, "giver");
        this.fullmektig = requireNonNull(fullmektig, "fullmektig");
        this.opprettetAv = opprettetAv == null ? "" : opprettetAv;
        this.opprettetDato = opprettetDato == null ? LocalDate.MIN : opprettetDato;
        this.endretAv = endretAv == null ? "" : endretAv;
        this.endretDato = endretDato == null ? LocalDate.MIN : endretDato;
    }

    public long getId() {
        return id;
    }

    public Fullmakttype getType() {
        return type;
    }

    public Fullmaktnivaa getNiva() {
        return niva;
    }

    public LocalDate getFomDato() {
        return fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
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

    public AktoerDto getGiver() {
        return giver;
    }

    public AktoerDto getFullmektig() {
        return fullmektig;
    }

    public String getOpprettetAv() {
        return opprettetAv;
    }

    public LocalDate getOpprettetDato() {
        return opprettetDato;
    }

    public String getEndretAv() {
        return endretAv;
    }

    public LocalDate getEndretDato() {
        return endretDato;
    }

    boolean isValidFor(String giverPid, String fullmektigPid, LocalDate date) {
        return isValidFor(giverPid, fullmektigPid, date, Fullmaktnivaa.FULLSTENDIG);
    }

    boolean isValidFor(String giverPid, String fullmektigPid, LocalDate date, Fullmaktnivaa fullmaktnivaa) {
        return gyldig
                && fagomraade.validForPensjon()
                && fullmaktnivaa.equals(niva)
                && !isInFuture(date) && !isExpired(date)
                && giver.isPerson(giverPid)
                && fullmektig.isPerson(fullmektigPid);
    }

    private boolean isInFuture(LocalDate date) {
        return date.isBefore(fomDato);
    }

    private boolean isExpired(LocalDate date) {
        return !lastsForever && tomDato.isBefore(date);
    }
}
