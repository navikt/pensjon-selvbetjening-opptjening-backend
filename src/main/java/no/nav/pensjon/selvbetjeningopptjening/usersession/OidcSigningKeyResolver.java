package no.nav.pensjon.selvbetjeningopptjening.usersession;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import no.nav.pensjon.selvbetjeningopptjening.security.CertificateGetter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

public abstract class OidcSigningKeyResolver extends SigningKeyResolverAdapter {

    private static final String CERTIFICATE_TYPE = "X.509";
    private final CertificateGetter certificateGetter;
    private final Map<String, Key> cachedKeysById;

    protected OidcSigningKeyResolver(CertificateGetter certificateGetter) {
        this.certificateGetter = requireNonNull(certificateGetter);
        this.cachedKeysById = new HashMap<>();
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        String keyId = header.getKeyId();
        return cachedKeysById.getOrDefault(keyId, getKey(keyId));
    }

    private Key getKey(String keyId) {
        String x509certificateString = getX509Certificate(keyId);

        try (var inStream = new ByteArrayInputStream(parseBase64Binary(x509certificateString))) {
            PublicKey key = getX509Certificate(inStream).getPublicKey();
            cachedKeysById.put(keyId, key);
            return key;
        } catch (IOException e) {
            throw new JwtException("Certificate stream handling error", e);
        }
    }

    private String getX509Certificate(String keyId) {
        try {
            return certificateGetter.getCertificate(keyId);
        } catch (CertificateException e) {
            return retry(keyId);
        }
    }

    private String retry(String keyId) {
        certificateGetter.refresh();

        try {
            return certificateGetter.getCertificate(keyId);
        } catch (CertificateException e) {
            throw new JwtException(String.format("Failed to get certificate for key ID '%s'", keyId), e);
        }
    }

    private static X509Certificate getX509Certificate(ByteArrayInputStream inStream) {
        try {
            return (X509Certificate) CertificateFactory
                    .getInstance(CERTIFICATE_TYPE)
                    .generateCertificate(inStream);
        } catch (CertificateException e) {
            throw new JwtException("Certificate creation error", e);
        }
    }
}
