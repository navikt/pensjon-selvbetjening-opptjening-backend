package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import no.nav.pensjon.selvbetjeningopptjening.common.selftest.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
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
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

@Component
public class UttaksgradConsumer implements UttaksgradGetter {

    private final String endpoint;
    private RestTemplate restTemplate;

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
            throw handle(e, "PROPEN3000 getUttaksgradForVedtak");
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(PEN, "PROPEN3000 getUttaksgradForVedtak", "An error occurred in the consumer", e);
        }
    }

    private String buildUrl(String endpoint, List<Long> vedtakIds) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(endpoint).path("/uttaksgrad/search");
        vedtakIds.forEach(vedtakId -> uriBuilder.queryParam("vedtakId", vedtakId));
        return uriBuilder.toUriString();
    }

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
            throw handle(e, "PROPEN3001 getAlderSakUttaksgradhistorikkForPerson");
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(PEN, "PROPEN3001 getAlderSakUttaksgradhistorikkForPerson", "An error occurred in the consumer", e);
        }
    }

    @Override
    public Optional<String> ping() {
        try {
            return Optional.ofNullable(restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint).path("/uttaksgrad/ping").toUriString(),
                    HttpMethod.GET,
                    null,
                    String.class).getBody());
        } catch (RestClientResponseException rce) {
            throw handle(rce, "Error in PEN Uttaksgrad");
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", "PEN", UriComponentsBuilder.fromHttpUrl(endpoint).path("/uttaksgrad/ping").toUriString());
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
