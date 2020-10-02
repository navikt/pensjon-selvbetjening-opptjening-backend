package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.util.SimpleStringExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ByProfileStrategyTest {

    private final Map<String, String> params = new HashMap<>();
    private TestByProfileStrategy strategy;
    @Mock
    UttaksgradGetter uttaksgradGetter;

    @BeforeEach
    void initialize() {
        strategy = new TestByProfileStrategy();
        params.clear();
    }

    @Test
    void getName_returns_strategyName() {
        String name = strategy.getName();
        assertEquals("byProfile", name);
    }

    @Test
    void isEnabled_returns_true_when_no_uttaksgrad_and_birthYear_is_after_1962() {
        setToggleValue("noUttakAndBornAfter1962");
        boolean isEnabled = strategy.isEnabled(params);
        assertTrue(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_birthYear_is_after_1962_but_uttaksgrad() {
        setToggleValue("noUttakAndBornAfter1962");
        when(uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(anyString())).thenReturn(singletonList(new Uttaksgrad()));

        boolean isEnabled = strategy.isEnabled(params);

        assertFalse(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_uttaksgrad_but_birthYear_is_notAfter_1962() {
        setToggleValue("noUttakAndBornAfter1962");
        when(uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(anyString())).thenReturn(singletonList(new Uttaksgrad()));
        strategy.setFnrBefore1962();

        boolean isEnabled = strategy.isEnabled(params);

        assertFalse(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_no_parameters() {
        boolean isEnabled = strategy.isEnabled(null);
        assertFalse(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_no_matching_profile() {
        setToggleValue("mismatch");
        boolean isEnabled = strategy.isEnabled(params);
        assertFalse(isEnabled);
    }

    private void setToggleValue(String value) {
        params.put("profile", value);
    }

    private class TestByProfileStrategy extends ByProfileStrategy {

        private String fnr = "01029312345";

        @Override
        @SuppressWarnings("unchecked")
        protected <T> T getBean(Class<T> beanClass) {
            return beanClass == StringExtractor.class ? (T) new SimpleStringExtractor(fnr) : (T) uttaksgradGetter;
        }

        void setFnrBefore1962() {
            fnr = "31126112345";
        }
    }
}
