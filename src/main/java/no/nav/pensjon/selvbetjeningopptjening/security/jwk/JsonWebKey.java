package no.nav.pensjon.selvbetjeningopptjening.security.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.jwk.KeyUse;

import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;

public class JsonWebKey {

    private final String keyId;
    private final String use;
    private final String algorithm;
    private final String n;
    private final String e;
    private final String d;
    private final String p;
    private final String q;
    private final String dp;
    private final String dq;
    private final String qi;

    /**
     * Creates a new JSON Web Key (JWK) with the specified parameters.
     *
     * @param keyId     The key identifier
     * @param use       The key use
     * @param algorithm The intended JOSE algorithm for the key
     * @param n         The modulus
     * @param e         The public exponent
     * @param d         The private exponent
     * @param p         The first prime factor
     * @param q         The second prime factor
     * @param dp        The first factor CRT exponent (CRT = Chinese Remainder Theorem)
     * @param dq        The second factor CRT exponent
     * @param qi        The first CRT coefficient
     */
    public JsonWebKey(String keyId,
                      String use,
                      String algorithm,
                      String n,
                      String e,
                      String d,
                      String p,
                      String q,
                      String dp,
                      String dq,
                      String qi) {
        this.keyId = keyId;
        this.use = use;
        this.algorithm = algorithm;
        this.n = n;
        this.e = e;
        this.d = d;
        this.p = p;
        this.q = q;
        this.dp = dp;
        this.dq = dq;
        this.qi = qi;
    }

    public String getKeyId() {
        return keyId;
    }

    public RSAPrivateKey getRsaPrivateKey() throws ParseException, JOSEException {
        return newRsaKey().toRSAPrivateKey();
    }

    private RSAKey newRsaKey() throws ParseException {
        return new RSAKey(
                new Base64URL(n),
                new Base64URL(e),
                new Base64URL(d),
                new Base64URL(p),
                new Base64URL(q),
                new Base64URL(dp),
                new Base64URL(dq),
                new Base64URL(qi),
                null,
                null,
                KeyUse.parse(use),
                null,
                new Algorithm(algorithm),
                keyId,
                null,
                null,
                null,
                null,
                null);
    }
}
