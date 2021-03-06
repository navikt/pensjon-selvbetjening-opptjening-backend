package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;

import java.time.LocalDate;
import java.util.List;

public class OpptjeningArguments {
    private final Person person;
    private final List<Restpensjon> restpensjoner;
    private final List<Uttaksgrad> uttaksgrader;
    private final AfpHistorikk afpHistorikk;
    private final UforeHistorikk uforeHistorikk;
    private final OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer;
    private final PensjonspoengConsumer pensjonspoengConsumer;
    private final PensjonsbeholdningConsumer beholdningConsumer;
    private final UttaksgradGetter uttaksgradGetter;

    OpptjeningArguments(Person person,
                        List<Restpensjon> restpensjoner,
                        List<Uttaksgrad> uttaksgrader,
                        AfpHistorikk afpHistorikk,
                        UforeHistorikk uforeHistorikk,
                        OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer,
                        PensjonspoengConsumer pensjonspoengConsumer,
                        PensjonsbeholdningConsumer beholdningConsumer,
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

    public OpptjeningsgrunnlagConsumer getOpptjeningsgrunnlagConsumer() {
        return opptjeningsgrunnlagConsumer;
    }

    public PensjonspoengConsumer getPensjonspoengConsumer() {
        return pensjonspoengConsumer;
    }

    public PensjonsbeholdningConsumer getBeholdningConsumer() {
        return beholdningConsumer;
    }

    public UttaksgradGetter getUttaksgradGetter() {
        return uttaksgradGetter;
    }
}
