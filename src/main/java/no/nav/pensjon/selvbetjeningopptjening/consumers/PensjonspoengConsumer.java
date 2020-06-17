package no.nav.pensjon.selvbetjeningopptjening.consumers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class PensjonspoengConsumer {
    private String endpoint = "https://wasapp-q2.adeo.no/popp-ws/api/pensjonspoeng";
    private RestTemplate restTemplate = new RestTemplate();
    private HentSystembrukerToken hentSystembrukerToken = new HentSystembrukerToken();

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

    @Value("${pensjonspoeng.endpoint.url}")
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    private String buildUrl(HentPensjonspoengListeRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path("/" + request.getFnr());

        return builder.toUriString();
    }


}
