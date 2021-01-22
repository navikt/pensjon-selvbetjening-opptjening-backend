package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;

@Component
public class ServiceUserTokenRestClient implements ServiceUserTokenGetter {

    private static final String USERNAME = System.getenv("SERVICEUSER_USERNAME");
    private static final String PASSWORD = System.getenv("SERVICEUSER_PASSWORD");
    private static final String AUTH_TYPE = "Basic";
    private final Log log = LogFactory.getLog(getClass());
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
    public ServiceUserToken getServiceUserToken() throws StsException {
        return isCachedTokenValid() ? lastFetchedUserToken : refreshedToken();
    }

    private ServiceUserToken fetch() throws StsException {
        log.debug("Retrieving new token for service user");

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
        } catch (RestClientException e) {
            log.error("Failed to acquire service user token: " + e.getMessage(), e);
            throw new StsException("Failed to access STS: " + e.getMessage(), e);
        }
    }

    private boolean isCachedTokenValid() {
        boolean valid = lastFetchedUserToken != null && !lastFetchedUserToken.isExpired(expirationLeeway);

        if (valid) {
            log.debug("Cached token is valid");
        }

        return valid;
    }

    private ServiceUserToken refreshedToken() throws StsException {
        ServiceUserToken token = fetch();
        lastFetchedUserToken = token;
        return token;
    }

    private static HttpEntity prepareRequestEntityWithAuth() {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, AUTH_TYPE + " " + getBasicAuthHeader());
        return new HttpEntity<>(headers);
    }

    private static String getBasicAuthHeader() {
        return Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes());
    }
}
