package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import io.jsonwebtoken.*;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class InternalUserJwsValidatorTest {

    private static final String CERTIFICATE_TYPE = "X.509";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String AUDIENCE = "aud";
    private static final String ISSUER = "iss";
    private static Key signingKey;
    private InternalUserJwsValidator validator;
    @Mock
    OidcConfigGetter oidcConfigGetter;
    @Mock
    SigningKeyResolver signingKeyResolver;

    @BeforeAll
    static void setUpAll() throws Exception {
        signingKey = getSigningKey(getCorrectCertificate());
    }

    @BeforeEach
    void setUp() {
        validator = new InternalUserJwsValidator(oidcConfigGetter, signingKeyResolver, AUDIENCE);
    }

    @Test
    void validate_returns_claims_when_valid_token() throws Exception {
        when(oidcConfigGetter.getIssuer()).thenReturn(ISSUER);
        when(signingKeyResolver.resolveSigningKey(any(JwsHeader.class), any(Claims.class))).thenReturn(signingKey);

        Jws<Claims> token = validator.validate(createToken(1));

        Claims claims = token.getBody();
        assertEquals(ISSUER, claims.getIssuer());
        assertEquals(AUDIENCE, claims.getAudience());
        assertEquals("Level4", claims.get("acr", String.class));
    }

    @Test
    void validate_throws_ExpiredJwtException_when_expired_token() {
        when(signingKeyResolver.resolveSigningKey(any(JwsHeader.class), any(Claims.class))).thenReturn(signingKey);
        assertThrows(ExpiredJwtException.class, () -> validator.validate(createToken(-1)));
    }

    @Test
    @SuppressWarnings("deprecation")
    void validate_throws_SignatureException_when_wrong_key_used() throws Exception {
        Key wrongSigningKey = getSigningKey(trimCertificate(asOneLine(getWrongCertificate())));
        when(signingKeyResolver.resolveSigningKey(any(JwsHeader.class), any(Claims.class))).thenReturn(wrongSigningKey);
        assertThrows(SignatureException.class, () -> validator.validate(createToken(1)));
    }

    @Test
    void validate_throws_MalformedJwtException_when_malformed_token() {
        assertThrows(MalformedJwtException.class, () -> validator.validate("malformed"));
    }

    private static Key getSigningKey(String certificate) throws CertificateException, IOException {
        try (var inStream = new ByteArrayInputStream(parseBase64Binary(certificate))) {
            return getX509Certificate(inStream).getPublicKey();
        }
    }

    private static X509Certificate getX509Certificate(ByteArrayInputStream inStream) throws CertificateException {
        return (X509Certificate) CertificateFactory
                .getInstance(CERTIFICATE_TYPE)
                .generateCertificate(inStream);
    }

    private static String createToken(int daysUntilExpiry) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Clock clock = Date::new;

        return Jwts.builder()
                .setClaims(prepareClaims())
                .setSubject("sub")
                .setAudience(AUDIENCE)
                .setIssuedAt(clock.now())
                .setId("jti")
                .setIssuer(ISSUER)
                .setExpiration(getDate(clock, daysUntilExpiry))
                .setHeaderParam("kid", "key-id")
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    private static Map<String, Object> prepareClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("acr", "Level4"); // Authentication Context class Reference
        return claims;
    }

    private static Date getDate(Clock clock, int daysFromNow) {
        var calendar = Calendar.getInstance();
        calendar.setTime(clock.now());
        calendar.add(Calendar.DATE, daysFromNow);
        return calendar.getTime();
    }

    private static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String keyBase64 = trimPrivateKey(asOneLine(getTestPrivateKey()));
        byte[] key = Base64.getDecoder().decode(keyBase64);
        var keySpec = new PKCS8EncodedKeySpec(key);
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(keySpec);
    }

    private static String asOneLine(String manyLines) throws IOException {
        var builder = new StringBuilder();

        try (var keyReader = new StringReader(manyLines);
             var lineReader = new BufferedReader(keyReader)) {
            String line;

            while ((line = lineReader.readLine()) != null) {
                builder.append(line);
            }
        }

        return builder.toString();
    }

    private static String trimPrivateKey(String key) {
        return key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
    }

    private static String trimCertificate(String certificate) {
        return certificate
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");
    }

    /**
     * Commands for creating self-signed certificate:
     * $ keytool -genkeypair -alias testkeys -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore selfsigned.p12 -validity 3650
     * $ openssl pkcs12 -in selfsigned.p12 -out cert.pem -clcerts -nokeys
     * The value between the BEGIN CERTIFICATE and END CERTIFICATE lines in cert.pem:
     */
    private static String getCorrectCertificate() {
        return "MIIDZTCCAk2gAwIBAgIEH4WXMzANBgkqhkiG9w0BAQsFADBjMQswCQYDVQQGEwJOTzENMAsGA1UECBMEb3NsbzENMAsGA1UEBxME" +
                "b3NsbzENMAsGA1UEChMEdG9sbDEPMA0GA1UECxMGc3lzdGVtMRYwFAYDVQQDEw1tb2NrLWlkcG9ydGVuMB4XDTE5MDMyNTE0MDg" +
                "yOVoXDTI5MDMyMjE0MDgyOVowYzELMAkGA1UEBhMCTk8xDTALBgNVBAgTBG9zbG8xDTALBgNVBAcTBG9zbG8xDTALBgNVBAoTBH" +
                "RvbGwxDzANBgNVBAsTBnN5c3RlbTEWMBQGA1UEAxMNbW9jay1pZHBvcnRlbjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCg" +
                "gEBAKLjLGzjeiuM4fUftsQfT+MkxZIWUUkmCUnl/lqAxx98bjO50AwrVY/eC+Kx83S0JTC6NxKcjCitB3ub0mQYmmAqdKRsHRj6" +
                "umpb9U1vbAmre6bFJ/KPeP+3ToGCo6gibn4/h8FpsPe579Xjfis8YGehkkpDun79nj4EvoD62zHJsJKMWziKsbKgKHrz21PtlQw" +
                "Y6BYe6FgqJqP+v/simwamxHk4nHIP7IRVw+bl5qKUuuSUklrh2eQZvCelICs2Nw+cdkn/XW4zEoSyLruytsTKFxtsSDDo4q3fDh" +
                "al1ZdvtuyEupg6n49QpcQ7G58YBSnMKbqGvSMXjDZ/ExxAVj0CAwEAAaMhMB8wHQYDVR0OBBYEFKtWjtwaQLjHhGkC9eCLBKDW+" +
                "n7PMA0GCSqGSIb3DQEBCwUAA4IBAQBCKLOiX2zXSZ2+J0cV5w1DhT308/tJHDIxrUd2NtmKSqvC2E0eskWg6kcJz+QBZm3mgUNK" +
                "m/fAINB8lEiekiIgWL9EjHaGe2wg8eqQA1f3kNpeN7XWN5yegwVLDC7OvMN9kScnPun6z6yaCL/fUia98Kxh2uxS8ub1Y0DfcnQ" +
                "luOpmHFLGvjAur3R1px8G3N2Haozggth/QYPWJVoe5wcoFlKBrxKoUxhwsFjnMKavK9D7DRQCd/b9WLuaOwgvrrKoRXv2vUO4u2" +
                "OgfA/eJy4P9wJH7182n3+VvTQzWNxIgWlBMM6d+EoIJ/GqLHriNyMkC/y8l4xa5hqI1B7lcbdQ";
    }

    private static String getWrongCertificate() {
        return "-----BEGIN CERTIFICATE-----\n" +
                "MIIDeTCCAmGgAwIBAgIEElPVVTANBgkqhkiG9w0BAQsFADBtMQswCQYDVQQGEwJO\n" +
                "TzENMAsGA1UECBMET3NsbzENMAsGA1UEBxMET3NsbzEMMAoGA1UEChMDTkFWMRow\n" +
                "GAYDVQQLExFEYXRhIG9nIFV0dmlrbGluZzEWMBQGA1UEAxMNRXNwZW4gR2pvc3Rv\n" +
                "bDAeFw0yMDA5MDMxMzAyMzRaFw0zMDA5MDExMzAyMzRaMG0xCzAJBgNVBAYTAk5P\n" +
                "MQ0wCwYDVQQIEwRPc2xvMQ0wCwYDVQQHEwRPc2xvMQwwCgYDVQQKEwNOQVYxGjAY\n" +
                "BgNVBAsTEURhdGEgb2cgVXR2aWtsaW5nMRYwFAYDVQQDEw1Fc3BlbiBHam9zdG9s\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4WoN914rIVF+ZmgNCN0L\n" +
                "CTg9HtwlfY3R8TLI4f8hiIaCsXQeeBD3fdowIKeVsqs5sX5cEvKaXpTTYt+QwQog\n" +
                "Ach7EDKdqEk6AUvHJ/7cxHNPzrFqe5yrtQbeA2SJfD7gFcWaZAEf6JPqL/SYZxt8\n" +
                "2fTob86/An3ewV0scuXlwyELSuYI6p71FXW/kQzm2eeQqclrukxCwI+ropkU0JBh\n" +
                "EkdB+wIv5hHIwJIAMwIylEXZ8ke3tQIbwMQ2mqfMKcY6wpNg7HZtO3lse8z0oElB\n" +
                "t22VLOCJLKwBvRnOzkkAlfcEbImEw0TjWyzqg6/zaefFAplXBYrfEMXuFgIN0t2n\n" +
                "OwIDAQABoyEwHzAdBgNVHQ4EFgQUeeEeB4aLabYLw6iU5jyR5deCLEkwDQYJKoZI\n" +
                "hvcNAQELBQADggEBALxOxjlosGhtzj/HZN5kZMR0BMCZE5LfgtLLr/e4l7Yo0NRy\n" +
                "/1W7zR647yBs5HnEFcoLQtU3CSsqbZ76WMqPERXpbRDqMeiXO9RRP0BmVEWT59ic\n" +
                "JBrh4ddHh1vGdwV7K+KjZ5XG9oCtDvoWvRIQJqvuFdUVmFU75hkbh3AXiBS82XUZ\n" +
                "oBNtbNNS6jcEPN7hCIPWz4b/V0NCpP9emi75ahE7GtGHz1qvo0j+jOlxHbK0Gui9\n" +
                "6TC0zgT4E0lxpFsrA2oL/zv19rFq/JTzY7hydWy6lZSqEC007SGUrARz1BsZx5fZ\n" +
                "78wxPb0KZ26NgEgFdSDVOs9rq0hybZBwvM/2aGU=\n" +
                "-----END CERTIFICATE-----";
    }

    /**
     * Gets a throwaway test key (no security breach).
     * Command for getting private key (given self-signed certificate as created above):
     * $ openssl pkcs12 -in selfsigned.p12 -nodes -nocerts -out private-key.pem
     * The content of private-key.pem:
     */
    private static String getTestPrivateKey() {
        return "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCi4yxs43orjOH1\n" +
                "H7bEH0/jJMWSFlFJJglJ5f5agMcffG4zudAMK1WP3gvisfN0tCUwujcSnIworQd7\n" +
                "m9JkGJpgKnSkbB0Y+rpqW/VNb2wJq3umxSfyj3j/t06BgqOoIm5+P4fBabD3ue/V\n" +
                "434rPGBnoZJKQ7p+/Z4+BL6A+tsxybCSjFs4irGyoCh689tT7ZUMGOgWHuhYKiaj\n" +
                "/r/7IpsGpsR5OJxyD+yEVcPm5eailLrklJJa4dnkGbwnpSArNjcPnHZJ/11uMxKE\n" +
                "si67srbEyhcbbEgw6OKt3w4WpdWXb7bshLqYOp+PUKXEOxufGAUpzCm6hr0jF4w2\n" +
                "fxMcQFY9AgMBAAECggEAZz38U4wfTGlAcwDF9CswlHUjMQQqQ2Xzbz5y6GRFUcSp\n" +
                "iOmWX5A77DM+b1KBUBA6nlb5PzewcruxSXyrbrxVZOyj/+9yxfltnKpqockkvkBN\n" +
                "4JeISWr8s4A5J0dpedFRHX4hCst12p6k0Hof50GejTDQq3egRotz+DVDljbqJfR/\n" +
                "XTt4z7GYGbDjcLBsLhrLBsGfKFWYZ2sJmkTKqH8oJAdqObrav06AU2USMu4lTpOI\n" +
                "Ex0yvhs6kiBFLPOAGFa0+yJrrR09KPahzxVDQ7ebkaUyk/lsMzqBpfqPIVd/uTDz\n" +
                "d4Ju2fb7Fy9H/ttEMJDc93htjyBBduIUT2Bk7vrIQQKBgQDiExWfYJlAbgFOFlfr\n" +
                "iiO7/3x5AqtZZvo8JbJXtlu3nz5JENdS7KSNUQ9zwj5+Q/Er8yLJklc4bIMKv9Si\n" +
                "8zjpGmkxdnA/wWVuXha4pvQQ0y2do09OIn5rhIKRxyYx74Oo/q1UPCXusBCC0LV1\n" +
                "2aAKDYaGy4UZqph4SpQODSqZEQKBgQC4cuLpG9/EwT8UwhOFpdctZ0aBXiDqbIQc\n" +
                "+L19kNi1Gd6WAxL1zbb0q1mOW4OINwsaZifMvoVWu9yu2Lhtz/+KKGe6fbRWied3\n" +
                "EJ4fGSVThRQAkiOYOpZKXeLhABYBzueCIJyicG6Du+3DJ5Dk/1Z7ezJMEg5MtXGn\n" +
                "Y++UEPqKbQKBgQCroieJT1Ip+xociZQIK2EDymxahq9F+YtC/K+QLc8l8czctd9z\n" +
                "gltPOlpL3Q2K2NQRNM6VT/fNy3wVPhwV9iefuUBhn/SKQq5aAy6m04F6AImiIykD\n" +
                "PebSg9CJEjOqN5eyRl+bEusNcjemyQLqzOTWnPwj/AE5wi1tLdmHbXGIsQKBgD7l\n" +
                "jlMHWkrxytz9QUL1xmaJLRW4T2khD/wBJjzHQ96dr7Vf3vtK5vSP6b4NntC4VGTw\n" +
                "KOo8naNx9FL7PAjWQMGP/a8uZmyMIg4L8J+SJ5RPcP6w1sp2UAGT+mXXbam4MGHD\n" +
                "VSZfSJBLaUx/FX3BRHBfFkuybIXj6Zm3Dl9jWswRAoGBAMjSiflXNl/Kold1NZla\n" +
                "shNQFkyF6axrdzFoRG6DXHQ8Ceput8mV1F3onx+0HBmAL2BPzlL5qQs8vlQvgDfm\n" +
                "w6UyLMmtseAW42xHI2qaZhWWJGThRj6ptOD1d4YqhgVtHOHbqr8POmP/rezxVuBB\n" +
                "dw7UFiKiAOPn79oWfIaFty2e\n" +
                "-----END PRIVATE KEY-----\n";
    }
}
