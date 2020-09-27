package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;

public class PdlConsumer {
    private static final Log LOGGER = LogFactory.getLog(PdlConsumer.class);
    private static final String ISSUER = "selvbetjening";
    private TokenValidationContextHolder context;
    private ServiceUserTokenGetter serviceUserTokenGetter;

    private WebClient webclient;

    public PdlConsumer(String endpoint, TokenValidationContextHolder context, ServiceUserTokenGetter serviceUserTokenGetter) {
        this.context = context;
        this.serviceUserTokenGetter = serviceUserTokenGetter;
        webclient = WebClient
                .builder()
                .baseUrl(endpoint)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Tema", "PEN")
                .build();
    }

    public PdlResponse getPdlResponse(PdlRequest request) {
        try {
            PdlResponse pdlResponse =
                    webclient.post()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + context.getTokenValidationContext().getJwtToken(ISSUER).getTokenAsString())
                            .header("Nav-Consumer-Token", "Bearer " + serviceUserTokenGetter.getServiceUserToken().getAccessToken())
                            .bodyValue(request.getGraphQlQuery())
                            .retrieve()
                            .bodyToMono(PdlResponse.class).block();
            if (pdlResponse == null) {
                LOGGER.error("PDL error: Failed parsing response");
                throw new FailedCallingExternalServiceException("PDL", "Failed parsing response");
            }
            if (pdlResponse.getErrors() != null && !pdlResponse.getErrors().isEmpty()) {
                StringBuilder errorListBuilder = new StringBuilder();
                errorListBuilder.append("Errors from PDL: ");
                pdlResponse.getErrors().forEach(error -> errorListBuilder.append(error.getMessage()).append(", "));

                LOGGER.error("PDL error: " + pdlResponse.getErrors().toString());
                throw new FailedCallingExternalServiceException("PDL", errorListBuilder.substring(0, errorListBuilder.length() - 2));
            }
            return pdlResponse;
        } catch (JSONException e) {
            LOGGER.error("PDL error: Failed deserializing JSON response", e);
            throw new FailedCallingExternalServiceException("PDL", "Failed deserializing JSON response");
        }
    }
}
