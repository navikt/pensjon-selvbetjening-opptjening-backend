package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PidTest {

    private static final String MONTH_ABOVE_9_BOST_NR = "01327200336";
    private static final String SPECIAL_FNR_0 = "26067300000";
    private static final String SPECIAL_FNR_1 = "26067300001";
    private static final String SPECIAL_FNR_2 = "26067300002";
    private static final String SPECIAL_FNR_29_FEB = "29027300000";
    private static final String SPECIAL_FNR_30_FEB = "30027300000";
    private static final String SPECIAL_FNR_61_MAY = "61057300000";
    private static final String SPECIAL_FNR_21_XXX = "21257300000";
    private static final String SPECIAL_FNR_ZERO = "00000000000";

    @Test
    void should_throw_PidValidationException_when_creating_Pid_with_invalid_inputs() {
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_0, false));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_1, false));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_2, true));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_2, false));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_29_FEB, false));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_30_FEB, true));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_30_FEB, false));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_61_MAY, false));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_21_XXX, false));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_ZERO, true));
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_ZERO, false));
    }

    @Test
    void should_validate_true_when_isDnummer_check_on_dnummer() {
        String dnummerValid1 = "61080098013";
        assertDoesNotThrow(() -> new Pid(dnummerValid1), "Failed check of valid dnummer:" + dnummerValid1);
    }

    @Test
    void should_validate_false_when_isDnummer_check_on_fnr() {
        assertDoesNotThrow(() -> new Pid(TestFnrs.NORMAL), "Failed check of dnummer with normal fnr:" + TestFnrs.NORMAL);
    }

    @Test
    void should_validate_true_when_isDummer_check_on_dnummer_with_special_circumstances() {
        String dnummerInvalidUnlessAcceptSpecialCircumstances = "41018100000";
        assertDoesNotThrow(() -> new Pid(dnummerInvalidUnlessAcceptSpecialCircumstances, true), "Failed check of special dnummer:" + dnummerInvalidUnlessAcceptSpecialCircumstances);
    }

    @Test
    void should_extract_expected_date_when_getFodselsdato_on_valid_Pid_from_fnr() {
        LocalDate expectedDate = LocalDate.of(1991, Month.FEBRUARY, 3);
        assertThat(new Pid(TestFnrs.NORMAL).getFodselsdato(), is(equalTo(expectedDate)));
    }

    @Test
    void should_extract_expected_date_when_getFodselsdato_on_valid_Pid_from_bostnr() {
        LocalDate expectedDateForAbove = LocalDate.of(1972, Month.DECEMBER, 1);
        LocalDate expectedDateForBelow = LocalDate.of(1901, Month.MAY, 4);
        assertThat(new Pid(MONTH_ABOVE_9_BOST_NR).getFodselsdato(), is(equalTo(expectedDateForAbove)));
        String monthBelow10BostNr = "04250100286";
        assertThat(new Pid(monthBelow10BostNr).getFodselsdato(), is(equalTo(expectedDateForBelow)));
    }

    @Test
    void should_throwPidValidationException_when_getFodselsdato_on_valid_special_circumstance_Pid_that_lacks_fodselsdato() {
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_0, true).getFodselsdato());
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_1, true).getFodselsdato());
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_29_FEB, true).getFodselsdato());
        assertThrows(PidValidationException.class, () -> new Pid(SPECIAL_FNR_21_XXX, true).getFodselsdato());
    }

    @Test
    void should_getFodselsdato_when_Pid_of_type_specialFnr61May() {
        LocalDate expecteFodselsdato = LocalDate.of(1973, Month.MAY, 21);
        assertThat(new Pid(SPECIAL_FNR_61_MAY, true).getFodselsdato(), is(expecteFodselsdato));
    }

    @Test
    void should_return_fnr_when_Pid_getPid_and_normalFnr() {
        Pid pid = new Pid(TestFnrs.NORMAL, false);
        assertThat(pid.toString(), is(equalTo(TestFnrs.NORMAL)));
    }
}
