package no.nav.pensjon.selvbetjeningopptjening.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class HentSystembrukerToken {
    private static final Logger LOGGER = LoggerFactory.getLogger(HentSystembrukerToken.class);

    private RestTemplate restTemplate;

    private String endpointUrl;
    private SystembrukerToken lastFetchedUserToken;
    private long expirationLeeway = 100000;
    private String authUsername = System.getenv("SERVICEUSER_USERNAME");
    private String authPassword = System.getenv("SERVICEUSER_PASSWORD");;

    public SystembrukerToken hentSystembrukerToken() {
        if (lastFetchedUserToken != null && !lastFetchedUserToken.isExpired(expirationLeeway)) {
            LOGGER.trace("Returning cached, non-expired token for service user");
            return lastFetchedUserToken;
        }
        return fetch();
    }

    private SystembrukerToken fetch() {
        LOGGER.trace("Retrieving new token for service user");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + getBasicAuthHeader());


        HttpEntity restRequest = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(endpointUrl)
                .queryParam("grant_type", "client_credentials")
                .queryParam("scope", "openid");

        SystembrukerToken hentSystembrukerTokenResponse;
        try {
            ResponseEntity<SystembrukerToken> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    restRequest,
                    SystembrukerToken.class);
            hentSystembrukerTokenResponse = response.getBody();
        } catch (HttpStatusCodeException e) {
            LOGGER.error("Feil ved henting av SystembrukerToken: " + e.getMessage(), e);
            return null;
        }
        return hentSystembrukerTokenResponse;
    }


    private String getBasicAuthHeader() {
        return java.util.Base64.getEncoder().encodeToString((authUsername + ":" + authPassword).getBytes());
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value(value = "${hentSystembrukerToken.endpoint.url}")
    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Value(value = "${hentUserToken.token.expiration.leeway}")
    public void setExpirationLeeway(String expirationLeeway) {
        this.expirationLeeway = Long.parseLong(expirationLeeway);
    }
}
