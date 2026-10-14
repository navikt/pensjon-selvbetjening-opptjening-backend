package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.BeholdningClient;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.OpptjeningClient;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.PensjonspoengClient;
import no.nav.pensjon.selvbetjeningopptjening.person.Person;

import java.util.List;

public class OpptjeningArguments {
    private final Person person;
    private final List<Restpensjon> restpensjoner;
    private final List<Uttaksgrad> uttaksgrader;
    private final AfpHistorikk afpHistorikk;
    private final UforeHistorikk uforeHistorikk;
    private final OpptjeningClient opptjeningsgrunnlagConsumer;
    private final PensjonspoengClient pensjonspoengConsumer;
    private final BeholdningClient beholdningConsumer;
    private final UttaksgradGetter uttaksgradGetter;

    public OpptjeningArguments(Person person,
                               List<Restpensjon> restpensjoner,
                               List<Uttaksgrad> uttaksgrader,
                               AfpHistorikk afpHistorikk,
                               UforeHistorikk uforeHistorikk,
                               OpptjeningClient opptjeningsgrunnlagConsumer,
                               PensjonspoengClient pensjonspoengConsumer,
                               BeholdningClient beholdningConsumer,
                               UttaksgradGetter uttaksgradGetter) {
        this.person = person;
        this.restpensjoner = restpensjoner;
        this.uttaksgrader = uttaksgrader;
        this.afpHistorikk = afpHistorikk;
        this.uforeHistorikk = uforeHistorikk;
        this.opptjeningsgrunnlagConsumer = opptjeningsgrunnlagConsumer;
        this.pensjonspoengConsumer = pensjonspoengConsumer;
        this.beholdningConsumer = beholdningConsumer;
        this.uttaksgradGetter = uttaksgradGetter;
    }

    public Person getPerson() {
        return person;
    }

    public List<Restpensjon> getRestpensjoner() {
        return restpensjoner;
    }

    public List<Uttaksgrad> getUttaksgrader() {
        return uttaksgrader;
    }

    public AfpHistorikk getAfpHistorikk() {
        return afpHistorikk;
    }

    public UforeHistorikk getUforeHistorikk() {
        return uforeHistorikk;
    }

    public OpptjeningClient getOpptjeningsgrunnlagConsumer() {
        return opptjeningsgrunnlagConsumer;
    }

    public PensjonspoengClient getPensjonspoengConsumer() {
        return pensjonspoengConsumer;
    }

    public BeholdningClient getBeholdningConsumer() {
        return beholdningConsumer;
    }

    public UttaksgradGetter getUttaksgradGetter() {
        return uttaksgradGetter;
    }
}