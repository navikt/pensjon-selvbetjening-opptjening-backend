package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.StsException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.AfpHistorikkMapper;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.UforeHistorikkMapper;
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

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

@Component
public class PersonConsumer implements Pingable {

    private static final String AFP_HISTORIKK_SERVICE = "PROPEN2602 getAfphistorikkForPerson";
    private static final String UFORE_HISTORIKK_SERVICE = "PROPEN2603 getUforehistorikkForPerson";
    private static final String PING_SERVICE = "PEN person ping";
    private static final String ENDPOINT_PATH = "/person";
    private static final String AUTH_TYPE = "Bearer";
    private final Log log = LogFactory.getLog(getClass());
    private final String endpoint;
    private final WebClient webClient;
    private final ServiceUserTokenGetter tokenGetter;

    public PersonConsumer(@Qualifier("epoch-support") WebClient webClient,
                          @Value("${pen.endpoint.url}") String endpoint,
                          ServiceUserTokenGetter tokenGetter) {
        this.webClient = requireNonNull(webClient);
        this.endpoint = requireNonNull(endpoint);
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    public AfpHistorikk getAfpHistorikkForPerson(String fnr) {
        try {
            var historikk = webClient
                    .get()
                    .uri(endpoint + ENDPOINT_PATH + "/afphistorikk")
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .header(PersonHttpHeaders.PID, fnr)
                    .retrieve()
                    .bodyToMono(AfpHistorikkDto.class)
                    .block();

            return AfpHistorikkMapper.fromDto(historikk);
        } catch (StsException e) {
            log.error(String.format("STS error in %s: %s", AFP_HISTORIKK_SERVICE, e.getMessage()), e);
            throw handle(e, AFP_HISTORIKK_SERVICE);
        } catch (WebClientResponseException e) {
            throw handle(e, AFP_HISTORIKK_SERVICE);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw handle(e, AFP_HISTORIKK_SERVICE);
        }
    }

    public UforeHistorikk getUforeHistorikkForPerson(String fnr) {
        try {
            var historikk = webClient
                    .get()
                    .uri(endpoint + ENDPOINT_PATH + "/uforehistorikk")
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .header(PersonHttpHeaders.PID, fnr)
                    .retrieve()
                    .bodyToMono(UforeHistorikkDto.class)
                    .block();

            return UforeHistorikkMapper.fromDto(historikk);
        } catch (StsException e) {
            log.error(String.format("STS error in %s: %s", UFORE_HISTORIKK_SERVICE, e.getMessage()), e);
            throw handle(e, UFORE_HISTORIKK_SERVICE);
        } catch (WebClientResponseException e) {
            throw handle(e, UFORE_HISTORIKK_SERVICE);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw handle(e, UFORE_HISTORIKK_SERVICE);
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
        return new PingInfo("REST", "PEN person", pingUri());
    }

    private String pingUri() {
        return UriComponentsBuilder.fromHttpUrl(endpoint).path(ENDPOINT_PATH + "/ping").toUriString();
    }

    private String getAuthHeaderValue() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getServiceUserToken().getAccessToken();
    }

    private static FailedCallingExternalServiceException handle(WebClientResponseException e, String serviceIdentifier) {
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
