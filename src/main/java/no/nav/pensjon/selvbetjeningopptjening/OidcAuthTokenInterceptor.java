package no.nav.pensjon.selvbetjeningopptjening;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import no.nav.pensjon.selvbetjeningopptjening.consumer.systembrukertoken.HentSystembrukerToken;

public class OidcAuthTokenInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private HentSystembrukerToken hentSystembrukerTokenService;

    public OidcAuthTokenInterceptor(HentSystembrukerToken hentSystembrukerToken){
        this.hentSystembrukerTokenService = hentSystembrukerToken;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logger.debug("Adding OIDC Authorization header to {} {}", request.getMethod(), request.getURI());

        try {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + hentSystembrukerTokenService.hentSystembrukerToken().getAccessToken());
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error when trying to get OIDC Token! Error Message: " + e.getMessage(), e);
            }
            return null;
        }
        return execution.execute(request, body);
    }
}
