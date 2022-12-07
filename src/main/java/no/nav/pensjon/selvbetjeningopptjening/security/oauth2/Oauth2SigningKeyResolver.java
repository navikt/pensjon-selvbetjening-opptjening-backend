package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import no.nav.pensjon.selvbetjeningopptjening.security.PublicKeyBasisGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.key.PublicKeyBasis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static jakarta.xml.bind.DatatypeConverter.parseBase64Binary;

@Component
public class Oauth2SigningKeyResolver extends SigningKeyResolverAdapter {

    private static final String CERTIFICATE_TYPE = "X.509";
    private static final Logger log = LoggerFactory.getLogger(Oauth2SigningKeyResolver.class);
    private final MultiIssuerSupport multiIssuerSupport;
    private final Map<String, Key> cachedKeysById;

    protected Oauth2SigningKeyResolver(MultiIssuerSupport multiIssuerSupport) {
        this.multiIssuerSupport = multiIssuerSupport;
        this.cachedKeysById = new HashMap<>();
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        String keyId = header.getKeyId();
        return cachedKeysById.getOrDefault(keyId, getKey(keyId, claims.getIssuer()));
    }

    private Key getKey(String keyId, String issuer) {
        PublicKeyBasis keyBasis = getPublicKeyBasis(keyId, issuer);
        PublicKey key = keyBasis.hasCertificate() ? getKeyFromCertificate(keyBasis) : getKeyFromModulus(keyBasis);
        cachedKeysById.put(keyId, key);
        return key;
    }

    private PublicKeyBasis getPublicKeyBasis(String keyId, String issuer) {
        PublicKeyBasisGetter basisGetter = null;

        try {
            basisGetter = multiIssuerSupport.getOauth2HandlerForIssuer(issuer).getPublicKeyBasisGetter();
            return basisGetter.getPublicKeyBasis(keyId);
        } catch (KeyException e) {
            log.info("Problem with key – retrying...", e);
            return retry(keyId, basisGetter);
        }
    }

    private PublicKeyBasis retry(String keyId, PublicKeyBasisGetter publicKeyBasisGetter) {
        publicKeyBasisGetter.refresh();

        try {
            return publicKeyBasisGetter.getPublicKeyBasis(keyId);
        } catch (KeyException e) {
            throw new JwtException(String.format("Failed to get public key basis for key ID '%s'", keyId), e);
        }
    }

    private PublicKey getKeyFromCertificate(PublicKeyBasis keyBasis) {
        try (var inStream = new ByteArrayInputStream(parseBase64Binary(keyBasis.getCertificate()))) {
            return getX509Certificate(inStream).getPublicKey();
        } catch (IOException e) {
            throw new JwtException("Certificate stream handling error", e);
        }
    }

    private RSAPublicKey getKeyFromModulus(PublicKeyBasis keyBasis) {
        try {
            return getRsaKey(keyBasis).toRSAPublicKey();
        } catch (ParseException | JOSEException e) {
            throw new JwtException("Failed to create public key from modulus/exponent", e);
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

    private static RSAKey getRsaKey(PublicKeyBasis basis) throws ParseException {
        return new RSAKey(
                new Base64URL(basis.getModulus()),
                new Base64URL(basis.getExponent()),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                KeyUse.parse(basis.getUse()),
                null,
                null,
                basis.getKeyId(),
                null,
                null,
                null,
                null,
                null);
    }
}
