package no.nav.pensjon.selvbetjeningopptjening.security.tokenx;

import com.nimbusds.jose.JOSEException;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import no.nav.pensjon.selvbetjeningopptjening.security.token.client.CacheAwareTokenClient;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.jwk.JsonWebKey;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2ParamBuilder;
import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.security.PrivateKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwtHeaderParamNames.*;

/**
 * Obtains access tokens for accessing other apps on behalf of the logged-in user.
 * This is relevant for external users (citizens).
 * Access tokens are obtained by exchanging the ID token obtained when the user logged in.
 */
@Component
@Qualifier("tokenx")
public class TokenExchangeClient extends CacheAwareTokenClient {

    private static final String TOKEN_TYPE = "JWT";
    private static final long MAX_EXPIRATION_INTERVAL_SECONDS = 120L;
    private static final long EXPIRATION_INTERVAL_SECONDS = MAX_EXPIRATION_INTERVAL_SECONDS;
    private static final Logger log = LoggerFactory.getLogger(TokenExchangeClient.class);
    private final Map<String, PrivateKey> keysById;
    private final String clientId;
    private final String jwk;

    public TokenExchangeClient(@Qualifier("tokenx") Oauth2ConfigGetter oauth2ConfigGetter,
                               ExpirationChecker expirationChecker,
                               @Value("${tokenx.openid.client-id}") String clientId,
                               @Value("${tokenx.openid.client-jwk}") String jwk) {
        super(webClient(), oauth2ConfigGetter, expirationChecker);
        this.clientId = requireNonNull(clientId, "clientId");
        this.jwk = requireNonNull(jwk, "jwk");
        this.keysById = new HashMap<>();
    }

    /**
     * Example audience: dev-fss:pensjonselvbetjening:pensjon-selvbetjening-fss-gateway
     */
    @Override
    protected MultiValueMap<String, String> prepareTokenRequestBody(TokenAccessParam accessParam, String audience) {
        try {
            return new Oauth2ParamBuilder()
                    .clientAssertion(createToken())
                    .tokenAccessParam(accessParam)
                    .audience(audience)
                    .buildClientAssertionTokenRequestMapForTokenX();
        } catch (JOSEException | JSONException | ParseException e) {
            throw new RuntimeException("Token creation failed", e);
        }
    }

    private String createToken() throws JSONException, ParseException, JOSEException {
        Clock clock = Date::new;
        JsonWebKey jsonWebKey = getJsonWebKey();
        PrivateKey key = getCachedPrivateKey(jsonWebKey);
        SignatureAlgorithm algorithm = SignatureAlgorithm.RS256;
        Date now = clock.now();

        return Jwts.builder()
                .setClaims(claims())
                .setId(randomId())
                .setAudience(getTokenEndpoint())
                .setIssuer(clientId)
                .setIssuedAt(now)
                .setExpiration(expirationFrom(now))
                .setNotBefore(now)
                .setSubject(clientId)
                .setHeaderParams(headerParams(jsonWebKey, algorithm))
                .signWith(key, algorithm)
                .compact();
    }

    private PrivateKey getCachedPrivateKey(JsonWebKey jsonWebKey) throws ParseException, JOSEException {
        return keysById.getOrDefault(
                jsonWebKey.getKeyId(),
                getFreshPrivateKey(jsonWebKey));
    }

    private PrivateKey getFreshPrivateKey(JsonWebKey jsonWebKey) throws ParseException, JOSEException {
        PrivateKey key = jsonWebKey.getRsaPrivateKey();
        keysById.put(jsonWebKey.getKeyId(), key);
        return key;
    }

    private JsonWebKey getJsonWebKey() throws JSONException {
        var json = new JSONObject(jwk);

        return new JsonWebKey(
                json.getString("kid"),
                json.getString("use"),
                json.optString("alg"),
                json.getString("n"),
                json.getString("e"),
                json.getString("d"),
                json.getString("p"),
                json.getString("q"),
                json.getString("dp"),
                json.getString("dq"),
                json.getString("qi"));
    }

    private static WebClient webClient() {
        var httpClient = HttpClient.create()
                .wiretap(true);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private static Map<String, Object> claims() {
        return Map.of("scope", "openid");
    }

    private static Map<String, Object> headerParams(JsonWebKey jsonWebKey, SignatureAlgorithm algorithm) {
        return Map.of(
                KEY_ID, jsonWebKey.getKeyId(),
                TYPE, TOKEN_TYPE,
                ALGORITHM, algorithm.getValue());
    }

    private static String randomId() {
        return randomUUID().toString();
    }

    private static Date expirationFrom(Date date) {
        Instant instant = date.toInstant().plusSeconds(EXPIRATION_INTERVAL_SECONDS);
        return Date.from(instant);
    }
}
