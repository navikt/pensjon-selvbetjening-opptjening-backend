package no.nav.pensjon.selvbetjeningopptjening.security.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * DES-based encryption/decryption for "semi-secret" values (e.g. OAuth2 state values).
 */
@Component
public class DesCrypto implements Crypto {

    private static final String ALGORITHM = "DES";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SecretKey secretKey;

    public DesCrypto(@Value("${crypto.key}") String key) throws CryptoException {
        secretKey = createSecretKey(key);
    }

    public String encrypt(String value) throws CryptoException {
        try {
            byte[] cleartext = value.getBytes(CHARSET);
            byte[] encryptedBytes = cipher(Cipher.ENCRYPT_MODE).doFinal(cleartext);
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw exception(e, "encrypt");
        }
    }

    public String decrypt(String value) throws CryptoException {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(value);
            byte[] decryptedBytes = cipher(Cipher.DECRYPT_MODE).doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw exception(e, "decrypt");
        }
    }

    private SecretKey createSecretKey(String key) throws CryptoException {
        try {
            var keySpec = new DESKeySpec(key.getBytes(CHARSET));
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw exception(e, "create secret key");
        }
    }

    private Cipher cipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        var cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKey);
        return cipher;
    }

    private CryptoException exception(GeneralSecurityException cause, String attemptedAction) {
        var message = String.format("Failed to %s: %s", attemptedAction, cause.getMessage());
        log.error(message);
        return new CryptoException(message, cause);
    }
}
