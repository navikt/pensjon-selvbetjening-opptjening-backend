package no.nav.pensjon.selvbetjeningopptjening.consumer.pen;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.TokenGetterFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

public abstract class AuthorizedPenConsumer {

    private static final String AUTH_TYPE = "Bearer";
    private static final Logger log = LoggerFactory.getLogger(AuthorizedPenConsumer.class);
    private final TokenGetterFacade tokenGetter;

    public AuthorizedPenConsumer(TokenGetterFacade tokenGetter) {
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    protected <T> T getObject(BiFunction<String, String, T> getter, String argument, String serviceName) {
        try {
            return getter.apply(argument, getAuthHeaderValue());
        } catch (StsException e) {
            log.error(String.format("STS error in %s: %s", serviceName, e.getMessage()), e);
            throw handle(e, serviceName);
        } catch (WebClientResponseException e) {
            throw handle(e, serviceName);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw handle(e, serviceName);
        }
    }

    private String getAuthHeaderValue() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getToken(AppIds.PENSJONSFAGLIG_KJERNE.appName);
    }

    private static FailedCallingExternalServiceException handle(WebClientResponseException e, String serviceIdentifier) {
        if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 401 UNAUTHORIZED", e);
        }

        if (e.getRawStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the provider, received 500 INTERNAL SERVER ERROR", e);
        }

        if (e.getRawStatusCode() == HttpStatus.BAD_REQUEST.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 400 BAD REQUEST", e);
        }

        return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the consumer", e);
    }

    private static FailedCallingExternalServiceException handle(StsException e, String service) {
        String cause = "Failed to acquire token for accessing " + service;
        return new FailedCallingExternalServiceException(PEN, service, cause, e);
    }

    private static FailedCallingExternalServiceException handle(RuntimeException e, String service) {
        return new FailedCallingExternalServiceException(PEN, service, "Failed to call service", e);
    }
}
