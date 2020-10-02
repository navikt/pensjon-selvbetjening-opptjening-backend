package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import no.nav.pensjon.selvbetjeningopptjening.util.SimpleStringExtractor;
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
        String encryptedUserId = new BCryptPasswordEncoder().encode("match");
        setToggleValue(encryptedUserId);

        boolean isEnabled = strategy.isEnabled(params);

        assertTrue(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_userId_mismatch() {
        String encryptedUserId = new BCryptPasswordEncoder().encode("match");
        setToggleValue(encryptedUserId);
        strategy.seMismatchingUserId();

        boolean isEnabled = strategy.isEnabled(params);

        assertFalse(isEnabled);
    }

    private void setToggleValue(String value) {
        params.put("user", value);
    }

    private static class TestByUserIdStrategy extends ByUserIdStrategy {

        private String userId = "match";

        @Override
        @SuppressWarnings("unchecked")
        protected <T> T getBean(Class<T> beanClass) {
            return (T) new SimpleStringExtractor(userId);
        }

        void seMismatchingUserId() {
            userId = "mismatch";
        }
    }
}
