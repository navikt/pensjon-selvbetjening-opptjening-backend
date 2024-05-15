package no.nav.pensjon.selvbetjeningopptjening.fullmakt.client;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.consumer.CustomHttpHeaders;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.NAV_CALL_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class FullmaktClient {

    private static final String SERVICE = "pensjon-representasjon";
    private static final String PATH = "/representasjon/hasValidRepresentasjonsforhold";
    public static final String VALID_REPRESENTASJONSTYPER_KEY = "validRepresentasjonstyper";
    public static final String INCLUDE_FULLMAKTSGIVER_NAME_PARAM = "includeFullmaktsgiverNavn";

    private static final List<String> VALID_REPRESENTASJONSTYPER = List.of(
            "PENSJON_FULLSTENDIG",
            "PENSJON_BEGRENSET",
            "PENSJON_SKRIV",
            "PENSJON_KOMMUNISER",
            "PENSJON_LES",
            "PENSJON_PENGEMOTTAKER",
            "PENSJON_VERGE",
            "PENSJON_VERGE_PENGEMOTTAKER",
            "PENSJON_SAMHANDLER",
            "PENSJON_SAMHANDLER_ADMIN");
    private final WebClient webClient;
    private final String url;

    public FullmaktClient(WebClient webClient,
                          @Value("${fullmakt.url}") String baseUrl) {
        this.webClient = requireNonNull(webClient, "webClient");
        this.url = requireNonNull(baseUrl, "baseUrl");
    }

    public RepresentasjonValidity hasValidRepresentasjonsforhold(String fullmaktsgiverPid) {

        try {
            return webClient
                    .get()
                    .uri(url())
                    .headers(h -> setHeaders(h, fullmaktsgiverPid))
                    .retrieve()
                    .bodyToMono(RepresentasjonValidity.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new FailedCallingExternalServiceException(SERVICE, "hasValidRepresentasjonsforhold", "Failed to call service: " + e.getResponseBodyAsString(), e);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw new FailedCallingExternalServiceException(SERVICE, "hasValidRepresentasjonsforhold", "Failed to call service", e);
        }
    }

    private String url() {
        return UriComponentsBuilder.fromHttpUrl(url)
                .path(PATH)
                .queryParam(VALID_REPRESENTASJONSTYPER_KEY, VALID_REPRESENTASJONSTYPER)
                .queryParam(INCLUDE_FULLMAKTSGIVER_NAME_PARAM, false)
                .build()
                .toUriString();
    }

    private static void setHeaders(HttpHeaders headers, String fullmaktsgiverPid) {
        headers.setBearerAuth(RequestContext.getEgressAccessToken(AppIds.FULLMAKT).getValue());
        headers.set(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        headers.set(CustomHttpHeaders.CALL_ID, MDC.get(NAV_CALL_ID));
        headers.set(CustomHttpHeaders.FULLMAKTSGIVER_PID, fullmaktsgiverPid);
    }
}
