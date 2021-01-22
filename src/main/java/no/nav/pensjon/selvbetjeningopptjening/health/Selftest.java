package no.nav.pensjon.selvbetjeningopptjening.health;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradConsumer;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@Component
public class Selftest {

    private final List<Pingable> services;

    public Selftest(OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer,
                    PensjonsbeholdningConsumer pensjonsbeholdningConsumer,
                    PensjonspoengConsumer pensjonspoengConsumer,
                    RestpensjonConsumer restpensjonConsumer,
                    PersonConsumer personConsumer,
                    UttaksgradConsumer uttaksgradConsumer,
                    PdlConsumer pdlConsumer) {
        this.services = List.of(
                opptjeningsgrunnlagConsumer,
                pensjonsbeholdningConsumer,
                pensjonspoengConsumer,
                restpensjonConsumer,
                personConsumer,
                uttaksgradConsumer,
                pdlConsumer);
    }

    /**
     * Returns result of selftest in JSON format.
     */
    String perform() {
        return format("[%s]", getServiceStatuses());
    }

    private String getServiceStatuses() {
        return services
                .stream()
                .map(this::getStatus)
                .collect(joining(","));
    }

    private String getStatus(Pingable service) {
        return format("{\"service\":\"%s\",\"status\":\"%s\"}",
                service.getPingInfo().getDescription(), ping(service));
    }

    private String ping(Pingable service) {
        try {
            service.ping();
            return "up";
        } catch (FailedCallingExternalServiceException e) {
            return "down";
        }
    }
}
