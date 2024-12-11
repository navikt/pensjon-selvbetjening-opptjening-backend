package no.nav.pensjon.selvbetjeningopptjening.fullmakt.client;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.consumer.CustomHttpHeaders;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.FullmaktsforholdDto;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.NAV_CALL_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class FullmaktClient {

    private static final String SERVICE = "Fullmakt";
    private static final String PATH = "/harFullmaktsforhold";
    private static final Logger log = LoggerFactory.getLogger(FullmaktClient.class);
    private final WebClient webClient;
    private final String url;

    public FullmaktClient(WebClient webClient,
                          @Value("${fullmakt.url}") String baseUrl) {
        this.webClient = requireNonNull(webClient, "webClient");
        this.url = requireNonNull(baseUrl, "baseUrl");
    }

    public FullmaktsforholdDto harFullmaktsforhold(String fullmaktsgiverPid, String fullmektigPid) {
        if (log.isDebugEnabled()) {
            log.debug("Calling {} for PID {}", SERVICE, maskFnr(fullmektigPid));
        }

        try {
            return webClient
                    .get()
                    .uri(url())
                    .headers(h -> setHeaders(h, fullmaktsgiverPid, fullmektigPid))
                    .retrieve()
                    .bodyToMono(FullmaktsforholdDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new FailedCallingExternalServiceException(SERVICE, "harFullmaktsforhold", "Failed to call service: " + e.getResponseBodyAsString(), e);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw new FailedCallingExternalServiceException(SERVICE, "harFullmaktsforhold", "Failed to call service", e);
        }
    }

    public RepresentasjonValidity hasValidRepresentasjonsforhold(String fullmaktsgiverPid, String fullmektigPid) {
        try {
            return webClient
                    .get()
                    .uri(url())
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(h -> setHeaders(h, fullmaktsgiverPid, fullmektigPid))
                    .header("fullmaktsgiverPid", fullmaktsgiverPid)
                    .retrieve()
                    .bodyToMono(RepresentasjonValidity.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new FailedCallingExternalServiceException(SERVICE, "hasValidRepresentasjonsforhold", "Failed to call service: " + e.getResponseBodyAsString(), e);
        }
    }

    private String url() {
        return UriComponentsBuilder.fromHttpUrl(url)
                .path(PATH)
                .build()
                .toUriString();
    }

    private static void setHeaders(HttpHeaders headers, String fullmaktsgiverPid, String fullmektigPid) {
        headers.setBearerAuth(RequestContext.getEgressAccessToken(AppIds.FULLMAKT).getValue());
        headers.set(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        headers.set(CustomHttpHeaders.CALL_ID, MDC.get(NAV_CALL_ID));
        headers.set(CustomHttpHeaders.FULLMAKTSGIVER_PID, fullmaktsgiverPid);
        headers.set(CustomHttpHeaders.FULLMEKTIG_PID, fullmektigPid);
    }
}
