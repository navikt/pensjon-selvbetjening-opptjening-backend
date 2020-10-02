package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ByEnvironmentStrategyTest {

    private static final String ENVIRONMENT_VARIABLE_VALUE = "env-match";
    private static final String PROPERTY_VALUE = "prop-match";
    private final Map<String, String> params = new HashMap<>();
    private ByEnvironmentStrategy strategy;

    @BeforeEach
    void initialize() {
        strategy = new TestByEnvironmentStrategy();
        params.clear();
    }

    @Test
    void getName_returns_strategyName() {
        String name = strategy.getName();
        assertEquals("byEnvironment", name);
    }

    @Test
    void isEnabled_returns_true_when_environmentVariable_matches() {
        setToggleValue(ENVIRONMENT_VARIABLE_VALUE);
        boolean isEnabled = strategy.isEnabled(params);
        assertTrue(isEnabled);
    }

    @Test
    void isEnabled_returns_true_when_property_matches() {
        setToggleValue(PROPERTY_VALUE);
        boolean isEnabled = strategy.isEnabled(params);
        assertTrue(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_no_property_match() {
        setToggleValue("mismatch");
        boolean isEnabled = strategy.isEnabled(params);
        assertFalse(isEnabled);
    }

    private void setToggleValue(String value) {
        params.put("miljø", value);
    }

    private static class TestByEnvironmentStrategy extends ByEnvironmentStrategy {
        @Override
        protected String getEnvironmentVariable(String name) {
            return ENVIRONMENT_VARIABLE_VALUE;
        }

        @Override
        protected String getProperty(String key, String defaultValue) {
            return PROPERTY_VALUE;
        }
    }
}
