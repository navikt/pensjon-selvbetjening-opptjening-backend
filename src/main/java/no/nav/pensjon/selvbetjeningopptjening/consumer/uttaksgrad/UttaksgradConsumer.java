package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonHttpHeaders;
import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.UttaksgradMapper;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

@Component
public class UttaksgradConsumer implements UttaksgradGetter, Pingable {

    private static final String UTTAKSGRAD_SERVICE = "PROPEN3000 getUttaksgradForVedtak";
    private static final String UTTAKSGRAD_HISTORIKK_SERVICE = "PROPEN3001 getAlderSakUttaksgradhistorikkForPerson";
    private static final String PING_SERVICE = "PEN uttaksgrad ping";
    private static final String ENDPOINT_PATH = "/uttaksgrad";
    private static final String AUTH_TYPE = "Bearer";
    private final Log log = LogFactory.getLog(getClass());
    private final String endpoint;
    private final WebClient webClient;
    private final ServiceTokenGetter tokenGetter;


    public UttaksgradConsumer(@Qualifier("epoch-support") WebClient webClient,
                              @Value("${pen.endpoint.url}") String endpoint,
                              ServiceTokenGetter tokenGetter) {
        this.webClient = requireNonNull(webClient);
        this.endpoint = endpoint;
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    @Override
    public List<Uttaksgrad> getUttaksgradForVedtak(List<Long> vedtakIds) {
        try {
            var response = webClient
                    .get()
                    .uri(buildUrl(endpoint, vedtakIds))
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .retrieve()
                    .bodyToMono(UttaksgradListResponse.class)
                    .block();

            return response == null ? null : UttaksgradMapper.fromDtos(response.getUttaksgradList());
        } catch (StsException e) {
            log.error(String.format("STS error in %s: %s", UTTAKSGRAD_SERVICE, e.getMessage()), e);
            throw handle(e, UTTAKSGRAD_SERVICE);
        } catch (WebClientResponseException e) {
            throw handle(e, UTTAKSGRAD_SERVICE);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw handle(e, UTTAKSGRAD_SERVICE);
        }
    }

    @Override
    public List<Uttaksgrad> getAlderSakUttaksgradhistorikkForPerson(String fnr) {
        try {
            var response = webClient
                    .get()
                    .uri(endpoint + ENDPOINT_PATH + "/person?sakType=ALDER")
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .header(PersonHttpHeaders.PID, fnr)
                    .retrieve()
                    .bodyToMono(UttaksgradListResponse.class)
                    .block();

            return response == null ? null : UttaksgradMapper.fromDtos(response.getUttaksgradList());
        } catch (StsException e) {
            log.error(String.format("STS error in %s: %s", UTTAKSGRAD_HISTORIKK_SERVICE, e.getMessage()), e);
            throw handle(e, UTTAKSGRAD_HISTORIKK_SERVICE);
        } catch (WebClientResponseException e) {
            throw handle(e, UTTAKSGRAD_HISTORIKK_SERVICE);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw handle(e, UTTAKSGRAD_HISTORIKK_SERVICE);
        }
    }

    @Override
    public void ping() {
        try {
            webClient
                    .get()
                    .uri(pingUri())
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (StsException e) {
            log.error(String.format("STS error in %s: %s", PING_SERVICE, e.getMessage()), e);
            throw handle(e, PING_SERVICE);
        } catch (WebClientResponseException e) {
            throw handle(e, PING_SERVICE);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw handle(e, PING_SERVICE);
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", "PEN uttaksgrad", pingUri());
    }

    private String pingUri() {
        return UriComponentsBuilder.fromHttpUrl(endpoint).path(ENDPOINT_PATH + "/ping").toUriString();
    }

    private String getAuthHeaderValue() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getServiceUserToken().getAccessToken();
    }

    private String buildUrl(String endpoint, List<Long> vedtakIds) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(endpoint).path(ENDPOINT_PATH + "/search");
        vedtakIds.forEach(vedtakId -> uriBuilder.queryParam("vedtakId", vedtakId));
        return uriBuilder.toUriString();
    }

    private FailedCallingExternalServiceException handle(WebClientResponseException e, String serviceIdentifier) {
        if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 401 UNAUTHORIZED", e);
        }

        if (e.getRawStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the provider, received 500 INTERNAL SERVER ERROR", e);
        }

        if (e.getRawStatusCode() == HttpStatus.BAD_REQUEST.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 400 BAD REQUEST", e);
        }

        return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the consumer", e);
    }

    private static FailedCallingExternalServiceException handle(StsException e, String service) {
        String cause = "Failed to acquire token for accessing " + service;
        return new FailedCallingExternalServiceException(PEN, service, cause, e);
    }

    private static FailedCallingExternalServiceException handle(RuntimeException e, String service) {
        return new FailedCallingExternalServiceException(PEN, service, "Failed to call service", e);
    }
}
