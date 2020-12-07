package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import no.nav.pensjon.selvbetjeningopptjening.security.CertificateGetter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Key;
import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class InternalUserSigningKeyResolverTest {

    private static final String KEY_ID = "jibNbkFSSbmxPYrN9CFqRk4K4gw";
    private InternalUserSigningKeyResolver signingKeyResolver;
    @Mock
    CertificateGetter certificateGetter;
    @Mock
    JwsHeader jwsHeader;
    @Mock
    Claims claims;

    @BeforeEach
    void initialize() {
        signingKeyResolver = new InternalUserSigningKeyResolver(certificateGetter);
    }

    @Test
    void resolveSigningKey_returns_key_when_certificate_ok() throws CertificateException {
        when(jwsHeader.getKeyId()).thenReturn(KEY_ID);
        when(certificateGetter.getCertificate(anyString())).thenReturn(prepareX509Certificate());

        Key key = signingKeyResolver.resolveSigningKey(jwsHeader, claims);

        assertEquals("X.509", key.getFormat());
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    void resolveSigningKey_throws_JwtException_when_certificate_not_found() throws CertificateException {
        when(jwsHeader.getKeyId()).thenReturn("unknown");
        when(certificateGetter.getCertificate(anyString())).thenThrow(new CertificateException("not found"));

        JwtException e = assertThrows(JwtException.class, () -> signingKeyResolver.resolveSigningKey(jwsHeader, claims));

        assertEquals("Failed to get certificate for key ID 'unknown'", e.getMessage());
    }

    @Test
    void resolveSigningKey_throws_JwtException_when_invalid_certificate() throws CertificateException {
        when(jwsHeader.getKeyId()).thenReturn(KEY_ID);
        when(certificateGetter.getCertificate(anyString())).thenReturn("invalid");

        JwtException e = assertThrows(JwtException.class, () -> signingKeyResolver.resolveSigningKey(jwsHeader, claims));

        assertEquals("Certificate creation error", e.getMessage());
    }

    private static String prepareX509Certificate() {
        return "MIIDBTCCAe2gAwIBAgIQUUG7iptQUoVA7bYvX2tHlDANBgkqhkiG9w0BAQsFADAtMSswKQYDVQQDEyJhY2NvdW50cy5hY2Nlc3Nj" +
                "b250cm9sLndpbmRvd3MubmV0MB4XDTIwMDcxODAwMDAwMFoXDTI1MDcxODAwMDAwMFowLTErMCkGA1UEAxMiYWNjb3VudHMuYWN" +
                "jZXNzY29udHJvbC53aW5kb3dzLm5ldDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANmF/mA7rk8z4momSrdOCLlMwI" +
                "wozyy5gQlQOp6SrmciKLjLmkhLjOwT/EHLXwqa1GNekatvig6wBL2miib7A+WTjXXbe/chXQrNRPMCpWCDzu+H0qXj3VQ4s35a+" +
                "VvkAhR/baJG2axwQbpbhaI4LW7zzFwIR32UxguCLWdIvy3PXl8LRDRyUAuHuJu6JcHitRMTPjs7Chsw5TrDlDlOaTdrB2O+J1z6" +
                "kER9XUCnwdDU9GK35G50fH4qvM/rKIsXxAlF5BfBc1pzJ6i+l/dMJ4J7iIvDrR9prw3J7L1FYdytiYL0oI0VmW7A8ehvplUC9rx" +
                "Lh1zF6CKHvIXdWaVdeqUCAwEAAaMhMB8wHQYDVR0OBBYEFFOUEOWLUJOTFTOlr7P+6GxsmM90MA0GCSqGSIb3DQEBCwUAA4IBAQ" +
                "CP+LLZw7SSYnWQmRGWHmksBwwJ4Gy32C6g7+wZZv3ombHW9mwLQuzsir97/PP042i/ZIxePHJavpeLm/z3KMSpGIPmiPtmgNcK4" +
                "HtLTEDnoTprnllobOAqU0TREFWogjkockNo98AvpsmHxNMXuwDikto9o/d9ACBtpkpatS2xgVOZxZtqyMpwZzSJARD5A4qcKov4" +
                "zdqntVyjpZGK4N6ZaedRbEVd12m1VI+dtDB9+EJRqtTn8zamPYljVTEPNCbDAFgKBDtrhwBnrrrnKTq4/LEOouNQZuUucBTMOGD" +
                "n4FEejNh3qbxNdWR6tSZbXUnJ+NIQ99IqZMvvMqm9ndL7";
    }
}
