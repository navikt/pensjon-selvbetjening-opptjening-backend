package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.time.LocalDate;

public class Restpensjon {

    private final LocalDate fomDate;
    private double restGrunnpensjon;
    private double restTilleggspensjon;
    private double restPensjonstillegg;

    public Restpensjon(LocalDate fomDate, Double restGrunnpensjon, Double restTilleggspensjon, Double restPensjonstillegg) {
        this.fomDate = fomDate;
        this.restGrunnpensjon = restGrunnpensjon == null ? 0D : restGrunnpensjon;
        this.restTilleggspensjon = restTilleggspensjon == null ? 0D : restTilleggspensjon;
        this.restPensjonstillegg = restPensjonstillegg == null ? 0D : restPensjonstillegg;
    }

    public LocalDate getFomDate() {
        return fomDate;
    }

    public double getRestGrunnpensjon() {
        return restGrunnpensjon;
    }

    public double getRestTilleggspensjon() {
        return restTilleggspensjon;
    }

    public double getRestPensjonstillegg() {
        return restPensjonstillegg;
    }
}
