package no.nav.pensjon.selvbetjeningopptjening.security.jwk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPrivateKey;

import static org.junit.jupiter.api.Assertions.*;

class JsonWebKeyTest {

    private static final String KEY_ID = "key-id";
    private JsonWebKey jsonWebKey;

    @BeforeEach
    void initialize() {
        jsonWebKey = newJsonWebKey();
    }

    @Test
    void test_getKeyId() {
        assertEquals(KEY_ID, jsonWebKey.getKeyId());
    }

    @Test
    void test_getRsaPrivateKey() throws Exception {
        RSAPrivateKey privateKey = jsonWebKey.getRsaPrivateKey();
        assertEquals("RSA", privateKey.getAlgorithm());
        assertEquals("PKCS#8", privateKey.getFormat());
    }

    /**
     * Private key parameters for testing purposes only.
     */
    private static JsonWebKey newJsonWebKey() {
        return new JsonWebKey(
                KEY_ID,
                "sig",
                "RS256",
                "q8RRN5ab8W7i3KgKde3CBM-l0lkLNmvgKuDbaF2ZtvW28jgMFQXXc2Yq8TkjxSy1GYuW9v43h5NsixLnfuFeXQjACChVWSGcF1M08BLaI089qcEckCUbKODD8ZrNYTL7ZxRZb1_kkx-TUHX-m67HIDrCK9Zy7abNRbiyjgGOJE5O-vzykysQi4KRR_kzv_yIbb9HJkBikd4YnajtPLN25TYYdxe2fITydYN1h8WaTiubNM9giGFlIX6QBU3x9DS9NDX7H7VEf98D64mMZt5VzMnHjQaugqdtmSiB66MAeTHTgN6G6fhBW-gGlXAVuoxrFAHGHmqnZM7eX_vz4mDg-Q",
                "AQAB",
                "owwNlFjQ2pNJoe-ZTmxMmciKPWvs6tHnzov3edAv067W5IIzZdlPtOkspiJ5Q_e2SPNqcDbMe1OD21gsixJfQqzFrHis25i91J5VR_Z9Prs_a142QNYYO-tv1G3_ut5sKmL0qnY6QTXd3qjC8QEtX_imzHoaSmDlxeMXCxmK1gY4mbMrpbG-AHa7lkbJRfmKCKU3balbaogNWE4XSQuzUlHxYulb5rrzuI9q4oolmf1fOO2QW6O-U-BwmEGWLuooAWW9dgHZ0jSonLpcjJNzebD3CSXsVpA9EH-4Z5iMInhJ0rpgWtf8u-89iMpsiIxNWUzbHv1TH2rzL-gJfH3SyQ",
                "39nYBxXr00AY8HxY5BAjoUXDReXDiWWx5UG2HamjkvxCNekJk4dDvCBSCCM45CrqQj30nuE7bpwqvPuTAHONvy4SGwKL2DKWTWC3jm_jNqHPys2bF5KSTLPrmNGUsCDu-tfam5FrVOMR46tm5JY4YXWMNl9QDST6saMuJxQAB2s",
                "xG-Jpt7gQ6n1PqvD_80zda129JLksUGwRV2H72_j8qI5uCZC4Wd6XnkyCePVYA6_9sbiwDt9IQgC7YtpnnuKO3KddNvwvhGOOucVvNoVsmKA97Xcb5Ih76rsf3Y_HrZpqJ_pclL-Ywc4BSuQmQkqS2NXR-n_cUSmwhIc7XQBZis",
                "m6I9BSgjjvuIm2MI9n-WFiGH08gcfCGdXrSiJttz49TJP-wnAVBRMrIR9qBuhIyl1Kp4siIMjSm8uSn8GZs9mQcyBV9u1UDOqTuuQzTdd3VnICx1QR1h5DxPeC92vkIXhYWf8vtW8WzyWiUoY5Nh3bi5ZHA-GvVz6x92DitkncM",
                "XGePnETpwEq6CBIq8DEqTOgdti1HkS6yad72rCa5VxEDD-JHwdq4kKp4ZHidjkNCACdM0VrqoNgw8GeNdrTssMxYz3kLBY4ilwjPi_gXDsQRoPUWzhEzD5gbClaomJz8lQsseNAbxJ_HtX7p1WOfVrlTfIvR0wmVXGPC4dyZL-M",
                "EQA1T3Qbr_dkcDicVSHY85vPdTW0QAHvr_za2ww62ZkOIRUWKYe9ZwAVqq9YAZM97KNLv1vlGuzwujY2sHARCOubJNRT9H8EBDj-3Z2BecycUydIGZ3P4LFc73jTRosiejhVDxK2JQ8siwNkyCeyxXxIwxYX4XZSpuvlVSyal8g");
    }
}
