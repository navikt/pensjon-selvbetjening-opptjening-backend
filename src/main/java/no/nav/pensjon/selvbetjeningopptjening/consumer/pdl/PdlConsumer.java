package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;

import java.util.List;

public class PdlConsumer {

    private static final String ISSUER = "selvbetjening";
    private static final String AUTH_TYPE = "Bearer";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private TokenValidationContextHolder context;
    private ServiceUserTokenGetter serviceUserTokenGetter;
    private WebClient webclient;

    public PdlConsumer(String endpoint, TokenValidationContextHolder context, ServiceUserTokenGetter serviceUserTokenGetter) {
        this.context = context;
        this.serviceUserTokenGetter = serviceUserTokenGetter;
        this.webclient = WebClient
                .builder()
                .baseUrl(endpoint)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Tema", "PEN")
                .build();
    }

    public PdlResponse getPdlResponse(PdlRequest request, boolean isInternalUser) {
        try {
            PdlResponse response =
                    webclient.post()
                            .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue(isInternalUser))
                            .header("Nav-Consumer-Token", consumerToken())
                            .bodyValue(request.getGraphQlQuery())
                            .retrieve()
                            .bodyToMono(PdlResponse.class).block();

            if (response == null) {
                log.error("PDL error: Failed parsing response");
                throw new FailedCallingExternalServiceException("PDL", "Failed parsing response");
            }

            handleErrors(response.getErrors());
            return response;
        } catch (JSONException e) {
            log.error("PDL error: Failed deserializing JSON response", e);
            throw new FailedCallingExternalServiceException("PDL", "Failed deserializing JSON response");
        }
    }

    private String getAuthHeaderValue(boolean isInternalUser) {
        return AUTH_TYPE + " " +
                (isInternalUser ? getServiceUserAccessToken() : getUserAccessToken());
    }

    private String getServiceUserAccessToken() {
        return serviceUserTokenGetter.getServiceUserToken().getAccessToken();
    }

    private String getUserAccessToken() {
        return context.getTokenValidationContext().getJwtToken(ISSUER).getTokenAsString();
    }

    private String consumerToken() {
        return AUTH_TYPE + " " + getServiceUserAccessToken();
    }

    private void handleErrors(List<PdlError> errors) {
        if (errors == null || errors.isEmpty()) {
            return;
        }

        var builder = new StringBuilder();
        builder.append("Errors from PDL: ");
        errors.forEach(error -> builder.append(error.getMessage()).append(", "));
        log.error("PDL error: " + errors.toString());
        throw new FailedCallingExternalServiceException("PDL", builder.substring(0, builder.length() - 2));
    }
}
