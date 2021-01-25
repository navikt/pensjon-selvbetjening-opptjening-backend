package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EgenAnsattCheckerTest {

    private static final Pid PID = new Pid(TestFnrs.NORMAL);
    private EgenAnsattChecker checker;

    @Mock
    SkjermingApi skjermingApi;

    @BeforeEach
    void setUp() {
        checker = new EgenAnsattChecker(skjermingApi);
    }

    @Test
    void isEgenAnsatt_returns_false_when_userIsNotSkjermet() {
        when(skjermingApi.isEgenAnsatt(PID)).thenReturn(false);
        assertFalse(checker.isEgenAnsatt(PID));
    }


    @Test
    void isEgenAnsatt_returns_true_when_userIsSkjermet() {
        when(skjermingApi.isEgenAnsatt(PID)).thenReturn(true);
        assertTrue(checker.isEgenAnsatt(PID));
    }
}
