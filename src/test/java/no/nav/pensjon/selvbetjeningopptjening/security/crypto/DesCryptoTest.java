package no.nav.pensjon.selvbetjeningopptjening.security.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DesCryptoTest {

    private static final String KEY = "abcdefgh";
    private static final String TOO_SHORT_KEY = "short";
    private DesCrypto crypto;

    @BeforeEach
    void initialize() throws CryptoException {
        crypto = new DesCrypto(KEY);
    }

    @Test
    void test_encrypt_ok() throws CryptoException {
        String encryptedValue = crypto.encrypt("value");
        assertEquals("286JvWBVqvo=", encryptedValue);
    }

    @Test
    void test_decrypt_ok() throws CryptoException {
        String decryptedValue = crypto.decrypt("286JvWBVqvo=");
        assertEquals("value", decryptedValue);
    }

    @Test
    void too_short_key_gives_cryptoException() {
        assertThrows(CryptoException.class, () -> new DesCrypto(TOO_SHORT_KEY));
    }

    @Test
    void decrypt_invalid_value_gives_cryptoException() {
        assertThrows(CryptoException.class, () -> crypto.decrypt("invalid"));
    }
}
