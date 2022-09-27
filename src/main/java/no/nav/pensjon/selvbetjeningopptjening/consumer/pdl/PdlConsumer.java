package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlError;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlErrorExtension;
import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping.PersonMapper.fromDto;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.NAV_CALL_ID;

@Component
public class PdlConsumer implements Pingable {

    private static final String CONSUMED_SERVICE = "PDL";
    private static final String PATH = "/graphql";
    private static final String TOKEN_ISSUER = "selvbetjening";
    private static final String AUTH_TYPE = "Bearer";
    private static final String THEME = "PEN";
    private static final Logger log = LoggerFactory.getLogger(PdlConsumer.class);
    private final TokenValidationContextHolder context;
    private final ServiceTokenGetter serviceUserTokenGetter;
    private final WebClient webClient;
    private final String url;

    public PdlConsumer(@Value("${pdl.url}") String baseUrl,
                       TokenValidationContextHolder context,
                       ServiceTokenGetter serviceUserTokenGetter) {
        this.context = requireNonNull(context, "context");
        this.url = requireNonNull(baseUrl, "baseUrl") + PATH;
        this.serviceUserTokenGetter = requireNonNull(serviceUserTokenGetter, "serviceUserTokenGetter");
        this.webClient = pdlWebClient();
    }

    public Person getPerson(Pid pid, LoginSecurityLevel securityLevel) throws PdlException {
        PdlResponse response = getPersonResponse(pid, securityLevel);
        handleErrors(response);
        return fromDto(response, pid);
    }

    @Override
    public void ping() {
        try {
            webClient
                    .options()
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, "", e.getResponseBodyAsString(), e);
        } catch (RuntimeException e) {
            // E.g. Exceptions$ReactiveException when connection is broken
            throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, e);
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", CONSUMED_SERVICE, url);
    }

    private PdlResponse getPersonResponse(Pid pid, LoginSecurityLevel securityLevel) {
        try {
            return webClient
                    .post()
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue(securityLevel))
                    .header(PdlHttpHeaders.CONSUMER_TOKEN, consumerToken())
                    .header(NAV_CALL_ID, MDC.get(NAV_CALL_ID))
                    .bodyValue(PdlRequest.getPersonQuery(pid))
                    .retrieve()
                    .bodyToMono(PdlResponse.class)
                    .retryWhen(Retry.backoff(4, Duration.ofMillis(500L)))
                    .block();
        } catch (IOException e) {
            return handleIoError(e);
        } catch (StsException e) {
            return handleStsError(e);
        } catch (WebClientResponseException e) {
            throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, "", e.getResponseBodyAsString(), e);
        } catch (RuntimeException e) {
            // E.g. Exceptions$ReactiveException when connection is broken
            throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, e);
        }
    }

    private String getAuthHeaderValue(LoginSecurityLevel securityLevel) throws StsException {
        return AUTH_TYPE + " " +
                (securityLevel == LoginSecurityLevel.INTERNAL
                        ? getServiceUserAccessToken()
                        : getUserAccessToken());
    }

    private WebClient pdlWebClient() {
        return WebClient
                .builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(PdlHttpHeaders.THEME, THEME)
                .build();
    }

    private String getServiceUserAccessToken() throws StsException {
        return serviceUserTokenGetter.getServiceUserToken().getAccessToken();
    }

    private String getUserAccessToken() {
        return context.getTokenValidationContext().getJwtToken(TOKEN_ISSUER).getTokenAsString();
    }

    private String consumerToken() throws StsException {
        return AUTH_TYPE + " " + getServiceUserAccessToken();
    }

    private PdlResponse handleIoError(IOException e) {
        String cause = "Error when trying to read graphQL-query from file";
        log.error(CONSUMED_SERVICE + " error: " + cause, e);
        throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, cause);
    }

    private PdlResponse handleStsError(StsException e) {
        String cause = "Failed to acquire token for accessing PDL";
        log.error(CONSUMED_SERVICE + " error: " + cause, e);
        throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, cause);
    }

    private void handleErrors(PdlResponse response) throws PdlException {
        if (response == null) {
            String cause = "Failed parsing response";
            log.error(CONSUMED_SERVICE + " error: " + cause);
            throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, cause);
        }

        handleErrors(response.getErrors());
    }

    private void handleErrors(List<PdlError> errors) throws PdlException {
        if (errors == null || errors.isEmpty()) {
            return;
        }

        handleSingleExtendedError(errors);

        String causes = errors.stream()
                .map(PdlError::getMessage)
                .collect(joining(", "));

        log.error(CONSUMED_SERVICE + " error: " + causes);
        throw new FailedCallingExternalServiceException(CONSUMED_SERVICE, causes);
    }

    private void handleSingleExtendedError(List<PdlError> errors) throws PdlException {
        if (errors.size() != 1) {
            return;
        }

        PdlError error = errors.get(0);
        PdlErrorExtension extensions = error.getExtensions();

        if (extensions == null || !StringUtils.hasText(extensions.getCode())) {
            return;
        }

        log.error(String.format("%s error: %s; %s", CONSUMED_SERVICE, error.getMessage(), extensions.getCode()));
        throw new PdlException(error.getMessage(), extensions.getCode());
    }
}
