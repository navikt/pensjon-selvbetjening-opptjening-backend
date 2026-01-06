package no.nav.pensjon.selvbetjeningopptjening.consumer.pen;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.EgressAccess;
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService;
import no.nav.pensjon.selvbetjeningopptjening.tech.security.masking.Masker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.BiFunction;

public abstract class AuthorizedPenConsumer {

    protected static final String PROVIDER = "PEN";
    private static final String AUTH_TYPE = "Bearer";
    private static final Logger log = LoggerFactory.getLogger(AuthorizedPenConsumer.class);

    protected <T> T getObject(BiFunction<String, String, T> getter, String argument, String serviceName) {
        if (log.isDebugEnabled()) {
            log.debug("Calling {} with argument {}", serviceName, Masker.INSTANCE.maskFnr(argument));
        }

        try {
            return getter.apply(argument, getAuthHeaderValue());
        } catch (WebClientResponseException e) {
            throw handle(e, serviceName);
        } catch (RuntimeException e) { // e.g., when connection broken
            throw handle(e, serviceName);
        }
    }

    private String getAuthHeaderValue() {
        return AUTH_TYPE + " " + EgressAccess.INSTANCE.token(EgressService.PENSJONSFAGLIG_KJERNE).getValue();
    }

    private static FailedCallingExternalServiceException handle(WebClientResponseException e, String serviceIdentifier) {
        return switch (e.getStatusCode()) {
            case HttpStatus.UNAUTHORIZED ->
                    new FailedCallingExternalServiceException(PROVIDER, serviceIdentifier, "Received 401 UNAUTHORIZED", e);
            case HttpStatus.INTERNAL_SERVER_ERROR ->
                    new FailedCallingExternalServiceException(PROVIDER, serviceIdentifier, "An error occurred in the provider, received 500 INTERNAL SERVER ERROR", e);
            case HttpStatus.BAD_REQUEST ->
                    new FailedCallingExternalServiceException(PROVIDER, serviceIdentifier, "Received 400 BAD REQUEST", e);
            default ->
                    new FailedCallingExternalServiceException(PROVIDER, serviceIdentifier, "An error occurred in the consumer", e);
        };
    }

    private static FailedCallingExternalServiceException handle(RuntimeException e, String service) {
        return new FailedCallingExternalServiceException(PROVIDER, service, "Failed to call service", e);
    }
}
