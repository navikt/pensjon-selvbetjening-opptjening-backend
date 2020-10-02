package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IsNotProdStrategyTest {

    private TestIsNotProdStrategy strategy;

    @BeforeEach
    void initialize() {
        strategy = new TestIsNotProdStrategy();
    }

    @Test
    void getName_returns_strategyName() {
        String name = strategy.getName();
        assertEquals("isNotProd", name);
    }

    @Test
    void isEnabled_returns_true_when_not_production() {
        strategy.setPropertyValue("not-prod");
        boolean isEnabled = strategy.isEnabled(null);
        assertTrue(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_production() {
        strategy.setPropertyValue("p");
        boolean isEnabled = strategy.isEnabled(null);
        assertFalse(isEnabled);
    }

    private static class TestIsNotProdStrategy extends IsNotProdStrategy {

        private String propertyValue;

        @Override
        protected String getProperty(String key, String defaultValue) {
            return propertyValue;
        }

        void setPropertyValue(String value) {
            propertyValue = value;
        }
    }
}
