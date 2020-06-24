package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

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

public class ServiceUserTokenGetter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUserTokenGetter.class);

    private RestTemplate restTemplate;

    private String endpointUrl;
    private ServiceUserToken lastFetchedUserToken;
    private long expirationLeeway = 100000;
    private String authUsername = System.getenv("SERVICEUSER_USERNAME");
    private String authPassword = System.getenv("SERVICEUSER_PASSWORD");;

    public ServiceUserToken getServiceUserToken() {
        if (lastFetchedUserToken != null && !lastFetchedUserToken.isExpired(expirationLeeway)) {
            LOGGER.trace("Returning cached, non-expired token for service user");
            return lastFetchedUserToken;
        }
        return fetch();
    }

    private ServiceUserToken fetch() {
        LOGGER.trace("Retrieving new token for service user");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + getBasicAuthHeader());


        HttpEntity restRequest = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(endpointUrl)
                .queryParam("grant_type", "client_credentials")
                .queryParam("scope", "openid");

        ServiceUserToken hentServiceUserTokenResponse;
        try {
            ResponseEntity<ServiceUserToken> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    restRequest,
                    ServiceUserToken.class);
            hentServiceUserTokenResponse = response.getBody();
        } catch (HttpStatusCodeException e) {
            LOGGER.error("Error when getting ServiceUserToken: " + e.getMessage(), e);
            return null;
        }
        return hentServiceUserTokenResponse;
    }


    private String getBasicAuthHeader() {
        return java.util.Base64.getEncoder().encodeToString((authUsername + ":" + authPassword).getBytes());
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value(value = "${serviceusertoken.endpoint.url}")
    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Value(value = "${serviceuser.token.expiration.leeway}")
    public void setExpirationLeeway(String expirationLeeway) {
        this.expirationLeeway = Long.parseLong(expirationLeeway);
    }
}
