package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

@Component
public class UttaksgradConsumer implements UttaksgradGetter {

    private String endpoint;
    private RestTemplate restTemplate;

    public UttaksgradConsumer(@Value("${pen.endpoint.url}") String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public List<Uttaksgrad> getUttaksgradForVedtak(List<Long> vedtakIdList) {
        ResponseEntity<UttaksgradListResponse> responseEntity;

        try {
            responseEntity = restTemplate.exchange(
                    buildUrl(endpoint, vedtakIdList),
                    HttpMethod.GET,
                    null,
                    UttaksgradListResponse.class);
        } catch (RestClientResponseException e) {
            throw handle(e, "PROPEN3000 getUttaksgradForVedtak");
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(PEN, "PROPEN3000 getUttaksgradForVedtak", "An error occurred in the consumer", e);
        }

        return responseEntity.getBody() != null ? responseEntity.getBody().getUttaksgradList() : null;
    }

    private String buildUrl(String endpoint, List<Long> vedtakIdList) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(endpoint).path("/uttaksgrad/search");
        vedtakIdList.forEach(vedtakId -> uriBuilder.queryParam("vedtakId", vedtakId));
        return uriBuilder.toUriString();
    }

    public List<Uttaksgrad> getAlderSakUttaksgradhistorikkForPerson(String fnr) {
        ResponseEntity<UttaksgradListResponse> responseEntity;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("pid", fnr);
            responseEntity = restTemplate.exchange(
                    endpoint + "/uttaksgrad/person?sakType=ALDER",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    UttaksgradListResponse.class);
        } catch (RestClientResponseException e) {
            throw handle(e, "PROPEN3001 getAlderSakUttaksgradhistorikkForPerson");
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(PEN, "PROPEN3001 getAlderSakUttaksgradhistorikkForPerson", "An error occurred in the consumer", e);
        }

        return responseEntity.getBody() != null ? responseEntity.getBody().getUttaksgradList() : null;
    }

    private FailedCallingExternalServiceException handle(RestClientResponseException e, String serviceIdentifier) {
        if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 401 UNAUTHORIZED", e);
        } else if (e.getRawStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the provider, received 500 INTERNAL SERVER ERROR", e);
        } else if (e.getRawStatusCode() == HttpStatus.BAD_REQUEST.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 400 BAD REQUEST", e);
        }
        return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the consumer", e);
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
