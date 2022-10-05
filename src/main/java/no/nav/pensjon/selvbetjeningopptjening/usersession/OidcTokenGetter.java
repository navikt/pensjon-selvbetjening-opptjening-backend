package no.nav.pensjon.selvbetjeningopptjening.usersession;

import no.nav.pensjon.selvbetjeningopptjening.security.dto.TokenResponseDto;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenDataMapper;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

public abstract class OidcTokenGetter implements TokenGetter { // cf. Oauth2TokenClient

    private static final Logger log = LoggerFactory.getLogger(OidcTokenGetter.class);
    private final WebClient webClient;
    private final OidcConfigGetter oidcConfigGetter;

    protected OidcTokenGetter(WebClient webClient, OidcConfigGetter oidcConfigGetter) {
        this.webClient = requireNonNull(webClient, "webClient");
        this.oidcConfigGetter = requireNonNull(oidcConfigGetter, "oidcConfigGetter");
    }

    @Override
    public TokenData getTokenData(TokenAccessParam accessParam, String unused) {
        TokenResponseDto body = webClient
                .post()
                .uri(oidcConfigGetter.getTokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(prepareTokenRequestBody(accessParam))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, this::handle4xxError)
                .bodyToMono(TokenResponseDto.class)
                .block();
        // Note: Do not use .body instead of .bodyValue, since this results in
        // chunked encoding,
        // which the endpoint may not support, resulting in 404 Not Found
        return TokenDataMapper.map(body);
    }

    protected abstract MultiValueMap<String, String> prepareTokenRequestBody(TokenAccessParam accessParam);

    private Mono<? extends Throwable> handle4xxError(ClientResponse response) {
        Mono<String> body = response.bodyToMono(String.class);

        return body.flatMap(message -> {
            log.error(message);
            throw new RuntimeException(message);
        });
    }
}
