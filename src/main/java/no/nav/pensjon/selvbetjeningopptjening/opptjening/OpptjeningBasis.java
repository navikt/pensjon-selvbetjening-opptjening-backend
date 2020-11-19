package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.*;

import java.util.List;

public class OpptjeningBasis {

    private final List<Pensjonspoeng> pensjonspoengList;
    private final List<Beholdning> beholdninger;
    private final List<Restpensjon> restpensjoner;
    private final List<Inntekt> inntekter;
    private final List<Uttaksgrad> uttaksgrader;
    private final AfpHistorikk afpHistorikk;
    private final UforeHistorikk uforeHistorikk;

    public OpptjeningBasis(List<Pensjonspoeng> pensjonspoengList,
                           List<Beholdning> beholdninger,
                           List<Restpensjon> restpensjoner,
                           List<Inntekt> inntekter,
                           List<Uttaksgrad> uttaksgrader,
                           AfpHistorikk afpHistorikk,
                           UforeHistorikk uforeHistorikk) {
        this.pensjonspoengList = pensjonspoengList;
        this.beholdninger = beholdninger;
        this.restpensjoner = restpensjoner;
        this.inntekter = inntekter;
        this.uttaksgrader = uttaksgrader;
        this.afpHistorikk = afpHistorikk;
        this.uforeHistorikk = uforeHistorikk;
    }

    List<Pensjonspoeng> getPensjonspoengList() {
        return pensjonspoengList;
    }

    List<Beholdning> getPensjonsbeholdninger() {
        return beholdninger;
    }

    List<Restpensjon> getRestpensjoner() {
        return restpensjoner;
    }

    List<Inntekt> getInntekter() {
        return inntekter;
    }

    List<Uttaksgrad> getUttaksgrader() {
        return uttaksgrader;
    }

    AfpHistorikk getAfpHistorikk() {
        return afpHistorikk;
    }

    UforeHistorikk getUforeHistorikk() {
        return uforeHistorikk;
    }
}
