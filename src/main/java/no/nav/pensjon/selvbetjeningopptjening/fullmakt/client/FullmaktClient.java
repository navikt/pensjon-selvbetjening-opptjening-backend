package no.nav.pensjon.selvbetjeningopptjening.fullmakt.client;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.consumer.CustomHttpHeaders;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.*;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.FullmakterDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktMapper.fullmakter;
import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.NAV_CALL_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class FullmaktClient {

    private static final String SERVICE = "Fullmakt";
    private static final String PATH = "/bprof/finnFullmakter";
    private static final String GYLDIG_QUERY_PARAM_NAME = "erGyldig";
    private static final Logger log = LoggerFactory.getLogger(FullmaktClient.class);
    private final WebClient webClient;
    private final String url;

    public FullmaktClient(WebClient webClient,
                          @Value("${fullmakt.url}") String baseUrl) {
        this.webClient = requireNonNull(webClient, "webClient");
        this.url = requireNonNull(baseUrl, "baseUrl");
    }

    public List<Fullmakt> getFullmakter(Pid pid, boolean erGyldig) {
        if (log.isDebugEnabled()) {
            log.debug("Calling {} for PID {}", SERVICE, maskFnr(pid));
        }

        try {
            FullmakterDto response = webClient
                    .get()
                    .uri(url(erGyldig))
                    .headers(h -> setHeaders(h).set(CustomHttpHeaders.AKTOER_NUMMER, pid.getPid()))
                    .retrieve()
                    .bodyToMono(FullmakterDto.class)
                    .block();

            return response == null ? emptyList() : fullmakter(response.aktor());
        } catch (WebClientResponseException e) {
            throw new FailedCallingExternalServiceException(SERVICE, "finnFullmakter", "Failed to call service: " + e.getResponseBodyAsString(), e);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw new FailedCallingExternalServiceException(SERVICE, "finnFullmakter", "Failed to call service", e);
        }
    }

    private String url(boolean gyldig) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .path(PATH)
                .queryParam(GYLDIG_QUERY_PARAM_NAME, gyldig)
                .build()
                .toUriString();
    }

    private static HttpHeaders setHeaders(HttpHeaders headers) {
        headers.setBearerAuth(RequestContext.getEgressAccessToken(AppIds.FULLMAKT).getValue());
        headers.set(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        headers.set(CustomHttpHeaders.CALL_ID, MDC.get(NAV_CALL_ID));
        return headers;
    }
}
