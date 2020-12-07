package no.nav.pensjon.selvbetjeningopptjening.usersession;

import no.nav.pensjon.selvbetjeningopptjening.security.crypto.Crypto;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2FlowException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class StateValidatorTest {

    private static final long TEST_TIME_MILLIS = 1234567890000L;
    private static final long TOO_OLD_TIME_MILLIS = 1000000000000L;
    private static final String VALID_URI = "/api/foo";
    private static final String INVALID_URI = "/invalid/foo";
    private static final String VALID_DELIMITER = ":";
    private static final String INVALID_DELIMITER = "|";
    private static final String ENCRYPTED_STATE = "cryptic";
    private TestStateValidator validator;
    @Mock
    private Crypto crypto;

    @BeforeEach
    void initialize() {
        validator = new TestStateValidator(crypto);
    }

    @Test
    void when_state_ok_then_redirectUri_returned() throws Exception {
        when(crypto.decrypt(ENCRYPTED_STATE)).thenReturn(TEST_TIME_MILLIS + VALID_DELIMITER + VALID_URI);
        String uri = validator.extractRedirectUri(ENCRYPTED_STATE);
        assertEquals("/api/foo", uri);
    }

    @Test
    void when_missing_state_then_exception() {
        String missingState = "";
        var exception = assertThrows(Oauth2FlowException.class, () -> validator.extractRedirectUri(missingState));
        assertEquals("Missing state", exception.getMessage());
    }

    @Test
    void when_invalid_format_then_exception() throws Exception {
        when(crypto.decrypt(ENCRYPTED_STATE)).thenReturn(TEST_TIME_MILLIS + INVALID_DELIMITER + VALID_URI);
        var exception = assertThrows(Oauth2FlowException.class, () -> validator.extractRedirectUri(ENCRYPTED_STATE));
        assertEquals("Invalid state format", exception.getMessage());
    }

    @Test
    void when_timestamp_too_old_then_exception() throws Exception {
        when(crypto.decrypt(ENCRYPTED_STATE)).thenReturn(TOO_OLD_TIME_MILLIS + VALID_DELIMITER + VALID_URI);
        var exception = assertThrows(Oauth2FlowException.class, () -> validator.extractRedirectUri(ENCRYPTED_STATE));
        assertEquals("Invalid state (1)", exception.getMessage());
    }

    private static class TestStateValidator extends StateValidator {

        TestStateValidator(Crypto crypto) {
            super(crypto);
        }

        @Override
        protected long getCurrentMillis() {
            return TEST_TIME_MILLIS;
        }
    }
}
