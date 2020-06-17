package no.nav.pensjon.selvbetjeningopptjening.consumers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class PensjonspoengConsumer {
    private final String endpoint;
    private RestTemplate restTemplate;
    private HentSystembrukerToken hentSystembrukerToken = new HentSystembrukerToken();

    public PensjonspoengConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public HentPensjonspoengListeResponse hentPensjonspoengListe(HentPensjonspoengListeRequest request) {
        ResponseEntity<HentPensjonspoengListeResponse> responseEntity;

        try {
            HttpHeaders headers = new HttpHeaders();
            //headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + hentSystembrukerToken.hentSystembrukerToken().getAccessToken());
            responseEntity = restTemplate.exchange(
                    buildUrl(request),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    HentPensjonspoengListeResponse.class);
        } catch (RestClientResponseException e) {
            return null;
        }

        return responseEntity.getBody();
    }

    private String buildUrl(HentPensjonspoengListeRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path("/" + request.getFnr());

        return builder.toUriString();
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
