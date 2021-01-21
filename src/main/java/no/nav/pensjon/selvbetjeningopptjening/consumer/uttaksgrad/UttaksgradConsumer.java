package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.UttaksgradMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

@Component
public class UttaksgradConsumer implements UttaksgradGetter, Pingable {

    private static final String UTTAKSGRAD_SERVICE = "PROPEN3000 getUttaksgradForVedtak";
    private static final String UTTAKSGRAD_HISTORIKK_SERVICE = "PROPEN3001 getAlderSakUttaksgradhistorikkForPerson";
    private static final String PING_SERVICE = "ping";
    private final String endpoint;
    private RestTemplate restTemplate;
    //TODO use WebClient

    public UttaksgradConsumer(@Value("${pen.endpoint.url}") String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public List<Uttaksgrad> getUttaksgradForVedtak(List<Long> vedtakIds) {
        try {
            UttaksgradListResponse response = restTemplate.exchange(
                    buildUrl(endpoint, vedtakIds),
                    HttpMethod.GET,
                    null,
                    UttaksgradListResponse.class)
                    .getBody();

            return response == null ? null : UttaksgradMapper.fromDtos(response.getUttaksgradList());
        } catch (RestClientResponseException e) {
            throw handle(e, UTTAKSGRAD_SERVICE);
        } catch (RestClientException e) {
            throw handle(e, UTTAKSGRAD_SERVICE);
        }
    }

    @Override
    public List<Uttaksgrad> getAlderSakUttaksgradhistorikkForPerson(String fnr) {
        try {
            UttaksgradListResponse response = restTemplate.exchange(
                    endpoint + "/uttaksgrad/person?sakType=ALDER",
                    HttpMethod.GET,
                    prepareHttpHeaders(fnr),
                    UttaksgradListResponse.class)
                    .getBody();

            return response == null ? null : UttaksgradMapper.fromDtos(response.getUttaksgradList());
        } catch (RestClientResponseException e) {
            throw handle(e, UTTAKSGRAD_HISTORIKK_SERVICE);
        } catch (RestClientException e) {
            throw handle(e, UTTAKSGRAD_HISTORIKK_SERVICE);
        }
    }

    @Override
    public void ping() {
        try {
            restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint).path("/uttaksgrad/ping").toUriString(),
                    HttpMethod.GET,
                    null,
                    String.class).getBody();
        } catch (RestClientResponseException e) {
            throw handle(e, PING_SERVICE);
        } catch (RestClientException e) {
            throw handle(e, PING_SERVICE);
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", "PEN uttaksgrad", UriComponentsBuilder.fromHttpUrl(endpoint).path("/uttaksgrad/ping").toUriString());
    }

    private String buildUrl(String endpoint, List<Long> vedtakIds) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(endpoint).path("/uttaksgrad/search");
        vedtakIds.forEach(vedtakId -> uriBuilder.queryParam("vedtakId", vedtakId));
        return uriBuilder.toUriString();
    }

    private FailedCallingExternalServiceException handle(RestClientResponseException e, String serviceIdentifier) {
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

    private static FailedCallingExternalServiceException handle(RestClientException e, String service) {
        return new FailedCallingExternalServiceException(PEN, service, "Failed to access service", e);
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static HttpEntity<HttpHeaders> prepareHttpHeaders(String fnr) {
        var headers = new HttpHeaders();
        headers.add("pid", fnr);
        return new HttpEntity<>(headers);
    }
}
