package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedsel;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlData;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlError;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping.BirthDateMapper.fromDtos;

@Component
public class PdlConsumer {

    private static final String CONSUMED_SERVICE = "PDL";
    private static final String TOKEN_ISSUER = "selvbetjening";
    private static final String AUTH_TYPE = "Bearer";
    private static final String THEME = "PEN";
    private final Log log = LogFactory.getLog(getClass());
    private final TokenValidationContextHolder context;
    private final ServiceUserTokenGetter serviceUserTokenGetter;
    private final WebClient webclient;

    public PdlConsumer(@Value("${pdl.endpoint.url}") String endpoint,
                       TokenValidationContextHolder context,
                       ServiceUserTokenGetter serviceUserTokenGetter) {
        this.context = context;
        this.serviceUserTokenGetter = serviceUserTokenGetter;
        this.webclient = WebClient
                .builder()
                .baseUrl(endpoint)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(PdlHttpHeaders.THEME, THEME)
                .build();
    }

    public List<BirthDate> getBirthDates(Pid pid, boolean isInternalUser) {
        try {
            PdlResponse response =
                    webclient.post()
                            .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue(isInternalUser))
                            .header(PdlHttpHeaders.CONSUMER_TOKEN, consumerToken())
                            .bodyValue(PdlRequest.getBirthQuery(pid))
                            .retrieve()
                            .bodyToMono(PdlResponse.class)
                            .block();

            handleErrors(response);
            return fromDtos(getBirths(response));
        } catch (JSONException e) {
            return handleJsonError(e);
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
        return context.getTokenValidationContext().getJwtToken(TOKEN_ISSUER).getTokenAsString();
    }

    private String consumerToken() {
        return AUTH_TYPE + " " + getServiceUserAccessToken();
    }

    private List<BirthDate> handleJsonError(JSONException e) {
        String cause = "Failed deserializing JSON response";
        log.error(CONSUMED_SERVICE + " error: " + cause, e);
        throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, cause);
    }

    private void handleErrors(PdlResponse response) {
        if (response == null) {
            String cause = "Failed parsing response";
            log.error(CONSUMED_SERVICE + " error: " + cause);
            throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, cause);
        }

        handleErrors(response.getErrors());
    }

    private void handleErrors(List<PdlError> errors) {
        if (errors == null || errors.isEmpty()) {
            return;
        }

        String causes = errors.stream()
                .map(PdlError::getMessage)
                .collect(joining(", "));

        log.error(CONSUMED_SERVICE + " error: " + causes);
        throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, causes);
    }

    private static List<Foedsel> getBirths(PdlResponse response) {
        return response == null
                ? emptyList()
                : getBirths(response.getData());
    }

    private static List<Foedsel> getBirths(PdlData data) {
        return data == null || data.getHentPerson() == null
                ? emptyList()
                : data.getHentPerson().getFoedsel();
    }
}
