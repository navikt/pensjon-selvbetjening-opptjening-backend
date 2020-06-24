package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class OidcAuthTokenInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ServiceUserTokenGetter serviceUserTokenGetterService;

    public OidcAuthTokenInterceptor(ServiceUserTokenGetter serviceUserTokenGetter){
        this.serviceUserTokenGetterService = serviceUserTokenGetter;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logger.debug("Adding OIDC Authorization header to {} {}", request.getMethod(), request.getURI());

        try {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + serviceUserTokenGetterService.getServiceUserToken().getAccessToken());
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error when trying to get OIDC Token! Error Message: " + e.getMessage(), e);
            }
            return null;
        }
        return execution.execute(request, body);
    }
}
