package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.mock.MockLoginInfoGetter;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfoGetter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
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
        when(uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(anyString())).thenReturn(singletonList(uttaksgrad()));

        boolean isEnabled = strategy.isEnabled(params);

        assertFalse(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_uttaksgrad_but_birthYear_is_notAfter_1962() {
        setToggleValue("noUttakAndBornAfter1962");
        when(uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(anyString())).thenReturn(singletonList(uttaksgrad()));
        strategy.setFnrBefore1962();

        boolean isEnabled = strategy.isEnabled(params);

        assertFalse(isEnabled);
    }

    @Test
    void isEnabled_returns_false_when_noUttakProfile_and_uttaksgrad() {
        setToggleValue("noUttak");
        when(uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(anyString())).thenReturn(singletonList(uttaksgrad()));
        strategy.setFnrBefore1962();

        boolean isEnabled = strategy.isEnabled(params);

        assertFalse(isEnabled);
    }

    @Test
    void isEnabled_returns_true_when_noUttakProfile_and_no_uttaksgrad() {
        setToggleValue("noUttak");
        when(uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(anyString())).thenReturn(emptyList());
        strategy.setFnrBefore1962();

        boolean isEnabled = strategy.isEnabled(params);

        assertTrue(isEnabled);
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

    private static Uttaksgrad uttaksgrad() {
        return new Uttaksgrad(null, null, LocalDate.MIN, null);
    }

    private class TestByProfileStrategy extends ByProfileStrategy {

        private static final String SYNTHETIC_FNR_BEFORE_1962 = "31126100666";
        private String fnr = "03029119367";

        @Override
        @SuppressWarnings("unchecked")
        protected <T> T getBean(Class<T> beanClass) {
            return beanClass == LoginInfoGetter.class ? (T) new MockLoginInfoGetter(fnr) : (T) uttaksgradGetter;
        }

        void setFnrBefore1962() {
            fnr = SYNTHETIC_FNR_BEFORE_1962;
        }
    }
}
