package no.nav.pensjon.selvbetjeningopptjening.consumer.sts;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2ParamNames;
import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.token.ServiceTokenData;
import no.nav.pensjon.selvbetjeningopptjening.security.token.ServiceTokenDataMapper;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import no.nav.pensjon.selvbetjeningopptjening.security.token.dto.ServiceTokenDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.http.BasicAuth.getBasicAuthHeader;

@Component
public class StsConsumer implements ServiceTokenGetter {

    private static final String PATH = "/rest/v1/sts/token";
    private static final Logger log = LoggerFactory.getLogger(StsConsumer.class);
    private final WebClient webClient;
    private final ExpirationChecker expirationChecker;
    private final String baseUrl;
    private final String authHeader;
    private ServiceTokenData tokenData;

    public StsConsumer(WebClient webClient,
                       ExpirationChecker expirationChecker,
                       @Value("${sts.url}") String baseUrl,
                       @Value("${sts.username}") String serviceUsername,
                       @Value("${sts.password}") String servicePassword) {
        this.webClient = requireNonNull(webClient, "webClient");
        this.expirationChecker = requireNonNull(expirationChecker, "expirationChecker");
        this.baseUrl = requireNonNull(baseUrl, "baseUrl") + PATH;
        this.authHeader = getBasicAuthHeader(serviceUsername, servicePassword);
    }

    @Override
    public ServiceTokenData getServiceUserToken() throws StsException {
        return isCachedTokenValid()
                ? tokenData
                : (tokenData = getFreshTokenData());
    }

    private ServiceTokenData getFreshTokenData() throws StsException {
        log.debug("Retrieving new token for service user");

        var uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam(Oauth2ParamNames.GRANT_TYPE, "client_credentials")
                .queryParam(Oauth2ParamNames.SCOPE, "openid");

        try {
            ServiceTokenDataDto data = webClient
                    .get()
                    .uri(uriBuilder.toUriString())
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(ServiceTokenDataDto.class)
                    .block();

            return ServiceTokenDataMapper.from(data, expirationChecker.time());
        } catch (WebClientResponseException e) {
            String message = String.format("Failed to acquire service user token. Message: %s. Response: %s.",
                    e.getMessage(), e.getResponseBodyAsString());
            log.error(message, e);
            throw new StsException(message, e);
        } catch (RuntimeException e) { // e.g. when connection broken
            String message = "Failed to access STS: " + e.getMessage();
            log.error(message, e);
            throw new StsException(message, e);
        }
    }

    private boolean isCachedTokenValid() {
        boolean valid = tokenData != null
                && !expirationChecker.isExpired(tokenData.getIssuedTime(), tokenData.getExpiresInSeconds());

        log.debug("Cached token is " + (valid ? "valid" : "invalid"));
        return valid;
    }
}
