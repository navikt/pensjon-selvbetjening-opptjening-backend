package no.nav.pensjon.selvbetjeningopptjening.security.oauth2.key;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;

/**
 * Holds information elements used to construct a public key.
 * A public key can be constructed from a certificate or from a modulus/exponent pair.
 */
public class PublicKeyBasis {

    private final String keyId;
    private final String use;
    private final String certificate;
    private final String exponent;
    private final String modulus;
    private final boolean hasCertificate;

    public static PublicKeyBasis certificateBased(String keyId, String use, String certificate) {
        return new PublicKeyBasis(keyId, use, certificate, "", "");
    }

    public static PublicKeyBasis modulusBased(String keyId, String use, String exponent, String modulus) {
        return new PublicKeyBasis(keyId, use, "", exponent, modulus);
    }

    public String getKeyId() {
        return keyId;
    }

    public String getUse() {
        return use;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getExponent() {
        return exponent;
    }

    public String getModulus() {
        return modulus;
    }

    public boolean hasCertificate() {
        return hasCertificate;
    }

    private PublicKeyBasis(String keyId, String use, String certificate, String exponent, String modulus) {
        this.keyId = requireNonNull(keyId, "keyId");
        this.use = requireNonNull(use, "use");
        this.certificate = certificate == null ? "" : certificate;
        this.exponent = exponent == null ? "" : exponent;
        this.modulus = modulus == null ? "" : modulus;
        this.hasCertificate = hasText(this.certificate);
    }
}
