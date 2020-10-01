package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ServiceUserTokenRestClient implements ServiceUserTokenGetter {

    private static final String USERNAME = System.getenv("SERVICEUSER_USERNAME");
    private static final String PASSWORD = System.getenv("SERVICEUSER_PASSWORD");
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private RestTemplate restTemplate;
    private String endpointUrl;
    private ServiceUserToken lastFetchedUserToken;
    private long expirationLeeway;

    public ServiceUserTokenRestClient(
            @Qualifier("conf.opptjening.resttemplate") RestTemplate restTemplate,
            @Value("${serviceusertoken.endpoint.url}") String endpointUrl,
            @Value("${serviceuser.token.expiration.leeway}") String expirationLeeway) {
        this.restTemplate = restTemplate;
        this.endpointUrl = endpointUrl;
        this.expirationLeeway = Long.parseLong(expirationLeeway);
    }

    @Override
    public ServiceUserToken getServiceUserToken() {
        return isCachedTokenValid() ? lastFetchedUserToken : refreshedToken();
    }

    private ServiceUserToken fetch() {
        logger.trace("Retrieving new token for service user");

        var uriBuilder = UriComponentsBuilder.fromHttpUrl(endpointUrl)
                .queryParam("grant_type", "client_credentials")
                .queryParam("scope", "openid");

        try {
            ResponseEntity<ServiceUserToken> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    prepareRequestEntityWithAuth(),
                    ServiceUserToken.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            logger.error("Error when getting ServiceUserToken: " + e.getMessage(), e);
            return null;
        }
    }

    private boolean isCachedTokenValid() {
        boolean valid = lastFetchedUserToken != null && !lastFetchedUserToken.isExpired(expirationLeeway);

        if (valid) {
            logger.trace("Cached token is valid");
        }

        return valid;
    }

    private ServiceUserToken refreshedToken() {
        ServiceUserToken token = fetch();
        lastFetchedUserToken = token;
        return token;
    }

    private static HttpEntity prepareRequestEntityWithAuth() {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + getBasicAuthHeader());
        return new HttpEntity<>(headers);
    }

    private static String getBasicAuthHeader() {
        return Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes());
    }
}
