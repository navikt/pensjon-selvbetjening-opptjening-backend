package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

public class UttaksgradConsumer {

    private String endpoint;
    private RestTemplate restTemplate;

    public UttaksgradConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Uttaksgrad> getUttaksgradForVedtak(List<Long> vedtakIdList) {
        ResponseEntity<UttaksgradListResponse> responseEntity;

        try {
            HttpHeaders headers = new HttpHeaders();
            responseEntity = restTemplate.exchange(
                    buildUrl(endpoint, vedtakIdList),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    UttaksgradListResponse.class);
        } catch (RestClientResponseException e) {
            return null;
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
            headers.add("fnr", fnr);
            responseEntity = restTemplate.exchange(
                    endpoint + "/uttaksgrad/person?sakType=ALDER",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    UttaksgradListResponse.class);
        } catch (RestClientResponseException e) {
            return null;
        }

        return responseEntity.getBody() != null ? responseEntity.getBody().getUttaksgradList() : null;
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
