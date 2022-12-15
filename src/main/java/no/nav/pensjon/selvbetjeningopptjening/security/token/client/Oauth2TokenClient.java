package no.nav.pensjon.selvbetjeningopptjening.security.token.client;

import no.nav.pensjon.selvbetjeningopptjening.security.dto.TokenResponseDto;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.TokenGetterException;
import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenDataMapper;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static java.util.Objects.requireNonNull;

public abstract class Oauth2TokenClient implements TokenGetter {

    private static final Logger log = LoggerFactory.getLogger(Oauth2TokenClient.class);
    private final WebClient webClient;
    private final ExpirationChecker expirationChecker;
    private final Oauth2ConfigGetter oauth2ConfigGetter;

    protected Oauth2TokenClient(WebClient webClient,
                                ExpirationChecker expirationChecker,
                                Oauth2ConfigGetter oauth2ConfigGetter) {
        this.webClient = requireNonNull(webClient);
        this.expirationChecker = requireNonNull(expirationChecker);
        this.oauth2ConfigGetter = requireNonNull(oauth2ConfigGetter);
    }

    @Override
    public TokenData getTokenData(TokenAccessParam accessParam, String audience) {
        log.debug("Getting token for audience '{}'...", audience);

        try {
            TokenResponseDto body = webClient
                    .post()
                    .uri(getTokenEndpoint())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(prepareTokenRequestBody(accessParam, audience))
                    .retrieve()
                    .bodyToMono(TokenResponseDto.class)
                    .block();
            // Note: Do not use .body instead of .bodyValue, since this results in chunked encoding,
            // which the endpoint may not support, resulting in 404 Not Found
            log.info("Token obtained for audience '{}'", audience);
            return TokenDataMapper.map(body, expirationChecker.time());
        } catch (WebClientResponseException e) {
            throw new TokenGetterException("Failed to obtain token: " + e.getResponseBodyAsString(), e);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw new TokenGetterException("Failed to obtain token", e);
        }
    }

    protected String getTokenEndpoint() {
        return oauth2ConfigGetter.getTokenEndpoint();
    }

    protected boolean isExpired(TokenData token) {
        return expirationChecker.isExpired(token.getIssuedTime(), token.getExpiresInSeconds());
    }

    protected abstract MultiValueMap<String, String> prepareTokenRequestBody(
            TokenAccessParam accessParam, String audience);
}
