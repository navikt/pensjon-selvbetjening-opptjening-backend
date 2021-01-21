package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class OidcAuthTokenInterceptor implements ClientHttpRequestInterceptor {

    private static final String AUTH_TYPE = "Bearer";
    private final Log log = LogFactory.getLog(getClass());
    private final ServiceUserTokenGetter tokenGetter;

    public OidcAuthTokenInterceptor(ServiceUserTokenGetter tokenGetter) {
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution requestExecution) throws IOException {
        log.debug(format("Adding OIDC authorization header to %s %s", request.getMethod(), request.getURI()));

        try {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, getAuth());
        } catch (StsException e) {
            throw new IOException("STS access error", e);
        }

        return requestExecution.execute(request, body);
    }

    private String getAuth() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getServiceUserToken().getAccessToken();
    }
}
