package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ByInstanceIdStrategyTest {

    private static final String PROPERTY_VALUE = "match";
    private final Map<String, String> params = new HashMap<>();
    private ByInstanceIdStrategy strategy;

    @BeforeEach
    void initialize() {
        strategy = new TestByInstanceIdStrategy();
        params.clear();
    }

    @Test
    void getName_returns_strategyName() {
        String name = strategy.getName();
        assertEquals("byInstanceId", name);
    }

    @Test
    void isEnabled_returns_true_when_the_only_value_matches() {
        setToggleValue(PROPERTY_VALUE);
        boolean isEnabled = strategy.isEnabled(params);
        assertTrue(isEnabled);
    }

    @Test
    void isEnabled_returns_true_when_one_of_many_values_matches() {
        setToggleValue(String.format("%s,%s,%s", "foo", PROPERTY_VALUE, "bar"));
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
        params.put("instance", value);
    }

    private static class TestByInstanceIdStrategy extends ByInstanceIdStrategy {
        @Override
        protected String getProperty(String key, String defaultValue) {
            return PROPERTY_VALUE;
        }
    }
}
