package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.config.SimpleLoginInfoGetter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ByUserIdStrategyTest {

    private final Map<String, String> params = new HashMap<>();
    private TestByUserIdStrategy strategy;

    @BeforeEach
    void initialize() {
        strategy = new TestByUserIdStrategy();
        params.clear();
    }

    @Test
    void getName_returns_strategyName() {
        String name = strategy.getName();
        assertEquals("byUserId", name);
    }

    @Test
    void isEnabled_returns_true_when_userId_match() {
        String encryptedUserId = new BCryptPasswordEncoder().encode(TestFnrs.NORMAL);
        setToggleValue(encryptedUserId);

        boolean isEnabled = strategy.isEnabled(params);

        assertTrue(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_userId_mismatch() {
        String encryptedUserId = new BCryptPasswordEncoder().encode(TestFnrs.NORMAL);
        setToggleValue(encryptedUserId);
        strategy.seMismatchingUserId();

        boolean isEnabled = strategy.isEnabled(params);

        assertFalse(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_encryptedUserId_fails_patternMatch() {
        String encryptedUserId = "failure";
        setToggleValue(encryptedUserId);

        boolean isEnabled = strategy.isEnabled(params);

        assertFalse(isEnabled);
    }

    private void setToggleValue(String value) {
        params.put("user", value);
    }

    private static class TestByUserIdStrategy extends ByUserIdStrategy {

        private String userId = TestFnrs.NORMAL;

        @Override
        @SuppressWarnings("unchecked")
        protected <T> T getBean(Class<T> beanClass) {
            return (T) new SimpleLoginInfoGetter(userId);
        }

        void seMismatchingUserId() {
            userId = "41015800001";
        }
    }
}
