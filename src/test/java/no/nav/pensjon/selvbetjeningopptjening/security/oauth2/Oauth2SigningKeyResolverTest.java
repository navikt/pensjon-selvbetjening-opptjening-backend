package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import no.nav.pensjon.selvbetjeningopptjening.security.PublicKeyBasisGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.key.PublicKeyBasis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Key;
import java.security.KeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class Oauth2SigningKeyResolverTest {

    private static final String KEY_ID = "jibNbkFSSbmxPYrN9CFqRk4K4gw";
    private static final String ISSUER = "iss1";
    private Oauth2SigningKeyResolver signingKeyResolver;

    @Mock
    private PublicKeyBasisGetter publicKeyBasisGetter;
    @Mock
    private MultiIssuerSupport multiIssuerSupport;
    @Mock
    private Claims claims;
    @SuppressWarnings("rawtypes")
    @Mock
    private JwsHeader jwsHeader;

    @BeforeEach
    void initialize() {
        signingKeyResolver = new Oauth2SigningKeyResolver(multiIssuerSupport);
        when(claims.getIssuer()).thenReturn(ISSUER);
        when(multiIssuerSupport.getOauth2HandlerForIssuer(ISSUER)).thenReturn(oauth2Handler());
    }

    @Test
    void resolveSigningKey_returns_key_when_publicKeyBasis_is_certificate() throws KeyException {
        when(jwsHeader.getKeyId()).thenReturn(KEY_ID);
        when(publicKeyBasisGetter.getPublicKeyBasis(KEY_ID)).thenReturn(basisWithValidX509Certificate());

        Key key = signingKeyResolver.resolveSigningKey(jwsHeader, claims);

        assertEquals("X.509", key.getFormat());
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    void resolveSigningKey_returns_key_when_publicKeyBasis_is_modulusAndExponent() throws KeyException {
        when(jwsHeader.getKeyId()).thenReturn(KEY_ID);
        when(publicKeyBasisGetter.getPublicKeyBasis(KEY_ID)).thenReturn(basisWithModulusAndExponent());

        Key key = signingKeyResolver.resolveSigningKey(jwsHeader, claims);

        assertEquals("X.509", key.getFormat());
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    void resolveSigningKey_throws_JwtException_when_publicKeyBasis_not_found() throws KeyException {
        when(jwsHeader.getKeyId()).thenReturn("unknown");
        when(publicKeyBasisGetter.getPublicKeyBasis("unknown")).thenThrow(new KeyException("not found"));

        var exception = assertThrows(JwtException.class, () -> signingKeyResolver.resolveSigningKey(jwsHeader, claims));

        assertEquals("Failed to get public key basis for key ID 'unknown'", exception.getMessage());
    }

    @Test
    void resolveSigningKey_throws_JwtException_when_invalid_publicKeyBasis() throws KeyException {
        when(jwsHeader.getKeyId()).thenReturn(KEY_ID);
        when(publicKeyBasisGetter.getPublicKeyBasis(anyString())).thenReturn(basisWithInvalidCertificate());

        var exception = assertThrows(JwtException.class, () -> signingKeyResolver.resolveSigningKey(jwsHeader, claims));

        assertEquals("Certificate creation error", exception.getMessage());
    }

    private Oauth2Handler oauth2Handler() {
        return new Oauth2Handler(
                publicKeyBasisGetter,
                "aud1",
                "key1",
                "key2",
                UserType.EXTERNAL);
    }

    private static PublicKeyBasis basisWithValidX509Certificate() {
        return PublicKeyBasis.certificateBased(
                "",
                "",
                "MIIDBTCCAe2gAwIBAgIQUUG7iptQUoVA7bYvX2tHlDANBgkqhkiG9w0BAQsFADAtMSswKQYDVQQDEyJhY2NvdW50cy5hY2Nlc3Nj" +
                        "b250cm9sLndpbmRvd3MubmV0MB4XDTIwMDcxODAwMDAwMFoXDTI1MDcxODAwMDAwMFowLTErMCkGA1UEAxMiYWNjb3VudHMuYWN" +
                        "jZXNzY29udHJvbC53aW5kb3dzLm5ldDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANmF/mA7rk8z4momSrdOCLlMwI" +
                        "wozyy5gQlQOp6SrmciKLjLmkhLjOwT/EHLXwqa1GNekatvig6wBL2miib7A+WTjXXbe/chXQrNRPMCpWCDzu+H0qXj3VQ4s35a+" +
                        "VvkAhR/baJG2axwQbpbhaI4LW7zzFwIR32UxguCLWdIvy3PXl8LRDRyUAuHuJu6JcHitRMTPjs7Chsw5TrDlDlOaTdrB2O+J1z6" +
                        "kER9XUCnwdDU9GK35G50fH4qvM/rKIsXxAlF5BfBc1pzJ6i+l/dMJ4J7iIvDrR9prw3J7L1FYdytiYL0oI0VmW7A8ehvplUC9rx" +
                        "Lh1zF6CKHvIXdWaVdeqUCAwEAAaMhMB8wHQYDVR0OBBYEFFOUEOWLUJOTFTOlr7P+6GxsmM90MA0GCSqGSIb3DQEBCwUAA4IBAQ" +
                        "CP+LLZw7SSYnWQmRGWHmksBwwJ4Gy32C6g7+wZZv3ombHW9mwLQuzsir97/PP042i/ZIxePHJavpeLm/z3KMSpGIPmiPtmgNcK4" +
                        "HtLTEDnoTprnllobOAqU0TREFWogjkockNo98AvpsmHxNMXuwDikto9o/d9ACBtpkpatS2xgVOZxZtqyMpwZzSJARD5A4qcKov4" +
                        "zdqntVyjpZGK4N6ZaedRbEVd12m1VI+dtDB9+EJRqtTn8zamPYljVTEPNCbDAFgKBDtrhwBnrrrnKTq4/LEOouNQZuUucBTMOGD" +
                        "n4FEejNh3qbxNdWR6tSZbXUnJ+NIQ99IqZMvvMqm9ndL7");
    }

    /**
     * Based on values from
     * https://navtestb2c.b2clogin.com/navtestb2c.onmicrosoft.com/discovery/v2.0/keys?p=b2c_1a_idporten_ver1
     */
    private static PublicKeyBasis basisWithModulusAndExponent() {
        return PublicKeyBasis.modulusBased(
                "id1",
                "sig",
                "AQAB",
                "uXBRESeJfP3eUmiiSFaFh519LWuf_6Inuwe__nX8NFJ57e2HMVs3XKRXOUr72Pzd0OQ5QLPt00nFNqSUalfxcvrhieWc55QrVlu1DyCOdKoMN5SGkfygPTcuvH16VBMAeJ2sy7aVNxToDocceZKtRhML2vqx0kEn5aZSB9Cj6AHPM7Edv2YAEt27kDWD8e4uEMqXSJaeU73bo2nwxtKfOQc1W1L8OovYm_jVgq3fFBX3JcsjwBoT-Pu180noDIzk9CaMDwgV0s7MIdrCZvHxdXJe_C_ZXHlC8teeQPXN9xQmwyGzXLAFoEZymPGal5FTtaSRYhROB7vVwuMwHMHZOQ");
    }

    private static PublicKeyBasis basisWithInvalidCertificate() {
        return PublicKeyBasis.certificateBased("", "", "invalid");
    }
}
