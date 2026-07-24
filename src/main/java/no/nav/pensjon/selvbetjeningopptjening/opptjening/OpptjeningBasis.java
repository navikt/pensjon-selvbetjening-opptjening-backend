package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.util.List;

public class OpptjeningBasis {

    private final List<Pensjonspoeng> pensjonspoengList;
    private final List<Beholdning> beholdninger;
    private final List<Restpensjon> restpensjoner;
    private final List<Inntekt> inntekter;
    private final List<Uttaksgrad> uttaksgrader;
    private final List<Uttaksgrad> uttaksgraderForBeholdningAfter2009;
    private final AfpHistorikk afpHistorikk;
    private final UforeHistorikk uforeHistorikk;

    public OpptjeningBasis(List<Pensjonspoeng> pensjonspoengList,
                           List<Beholdning> beholdninger,
                           List<Restpensjon> restpensjoner,
                           List<Inntekt> inntekter,
                           List<Uttaksgrad> uttaksgrader,
                           List<Uttaksgrad> uttaksgraderForBeholdningAfter2009,
                           AfpHistorikk afpHistorikk,
                           UforeHistorikk uforeHistorikk) {
        this.pensjonspoengList = pensjonspoengList;
        this.beholdninger = beholdninger;
        this.restpensjoner = restpensjoner;
        this.inntekter = inntekter;
        this.uttaksgrader = uttaksgrader;
        this.uttaksgraderForBeholdningAfter2009 = uttaksgraderForBeholdningAfter2009;
        this.afpHistorikk = afpHistorikk;
        this.uforeHistorikk = uforeHistorikk;
    }

    public List<Pensjonspoeng> getPensjonspoengList() {
        return pensjonspoengList;
    }

    public List<Beholdning> getPensjonsbeholdninger() {
        return beholdninger;
    }

    public List<Restpensjon> getRestpensjoner() {
        return restpensjoner;
    }

    public List<Inntekt> getInntekter() {
        return inntekter;
    }

    public List<Uttaksgrad> getUttaksgrader() {
        return uttaksgrader;
    }

    public List<Uttaksgrad> getUttaksgraderForBeholdningAfter2009() {
        return uttaksgraderForBeholdningAfter2009;
    }

    public AfpHistorikk getAfpHistorikk() {
        return afpHistorikk;
    }

    public UforeHistorikk getUforeHistorikk() {
        return uforeHistorikk;
    }
}
