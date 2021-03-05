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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

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
     * Returns result of selftest in HTML format.
     */
    String performHtml() {
        return htmlPage(htmlStatusRows(perform()));
    }

    /**
     * Returns result of selftest in JSON format.
     */
    String performJson() {
        return format("[%s]", getServiceStatuses());
    }

    private Map<String, Boolean> perform() {
        return services
                .stream()
                .collect(toMap(
                        service -> service.getPingInfo().getDescription(),
                        service -> "up".equalsIgnoreCase(ping(service)),
                        (a, b) -> b, HashMap::new));
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

    private static String htmlPage(String tableRows) {
        return format("<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Pensjon Selvbetjening Opptjening selvtest</title>" +
                "</head>" +
                "<body>" +
                "<div>" +
                "<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Tjeneste</th>" +
                "<th>Status</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" +
                "%s" +
                "</tbody>" +
                "</table>" +
                "</div>" +
                "</body>" +
                "</html>", tableRows);
    }

    private static String htmlStatusRows(Map<String, Boolean> map) {
        return map
                .entrySet()
                .stream()
                .map(e -> htmlRow(e.getKey(), e.getValue()))
                .collect(joining(""));
    }

    private static String htmlRow(String service, boolean ok) {
        return format("<tr><td>%s</td>%s</tr>", service, htmlStatusCell(ok));
    }

    private static String htmlStatusCell(boolean ok) {
        String color = ok ? "green" : "red";
        String status = ok ? "up" : "DOWN";
        return format("<td style=\"background-color:%s;text-align:center;\">%s</td>", color, status);
    }
}
