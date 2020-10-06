package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class OidcAuthTokenInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ServiceUserTokenGetter serviceUserTokenGetter;

    public OidcAuthTokenInterceptor(ServiceUserTokenGetter serviceUserTokenGetter) {
        this.serviceUserTokenGetter = serviceUserTokenGetter;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        logger.debug("Adding OIDC Authorization header to {} {}", request.getMethod(), request.getURI());

        try {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + serviceUserTokenGetter.getServiceUserToken().getAccessToken());
            return execution.execute(request, body);
        } catch (Exception e) {
            logger.error("Error when trying to get OIDC token: " + e.getMessage(), e);
            return null;
        }
    }
}
