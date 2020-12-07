package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private static final String SPECIAL_FNR_3_JAN_76 = "03017600000";
    private static final String SPECIAL_FNR_3_JAN_73 = "03017300000"; // Passes the mod11 check, but not the special circumstance check
    private static final String D_NUMMER_VALID_LOW_PNR_1 = "41015800001";
    private static final String D_NUMMER_VALID_LOW_PNR_2 = "57022800002";
    private static final String D_NUMMER_VALID_LOW_PNR_3 = "64025000003";
    private static final String D_NUMMER_VALID_LOW_PNR_4 = "58044400004";
    private static final String D_NUMMER_VALID_LOW_PNR_5 = "61073600005";
    private static final String D_NUMMER_VALID_LOW_PNR_6 = "67045600006";
    private static final String D_NUMMER_VALID_LOW_PNR_7 = "46073400007";
    private static final String D_NUMMER_VALID_LOW_PNR_8 = "70103900008";
    private static final String D_NUMMER_VALID_LOW_PNR_9 = "41033100009";

    @Test
    void should_validate_false_when_special_circumstances_not_allowed_and_pid_has_special_circumstances() {
        assertFalse(Pid.isValidPid(SPECIAL_FNR_0, false), "Special circumstance test without acceptspecialcircumstance set failed for Fnr" + SPECIAL_FNR_0);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_0), "Special circumstance test without acceptspecialcircumstance set failed for Fnr" + SPECIAL_FNR_0);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_1, false), "Special circumstance test without acceptspecialcircumstance set failed for Fnr" + SPECIAL_FNR_1);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_1), "Special circumstance test without acceptspecialcircumstance set failed for Fnr" + SPECIAL_FNR_1);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_2), "Special circumstance test failed for Fnr" + SPECIAL_FNR_2);

        assertFalse(Pid.isValidPid(SPECIAL_FNR_29_FEB), "Special circumstance test failed for Fnr" + SPECIAL_FNR_29_FEB);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_29_FEB, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_29_FEB);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_30_FEB), "Special circumstance test failed for Fnr" + SPECIAL_FNR_30_FEB);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_30_FEB, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_29_FEB);

        assertFalse(Pid.isValidPid(SPECIAL_FNR_61_MAY), "Special circumstance test failed for Fnr" + SPECIAL_FNR_61_MAY);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_61_MAY, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_61_MAY);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_21_XXX), "Special circumstance test failed for Fnr" + SPECIAL_FNR_21_XXX);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_21_XXX, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_21_XXX);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_ZERO), "Special circumstance test failed for Fnr" + SPECIAL_FNR_ZERO);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_ZERO, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_ZERO);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_3_JAN_73, false), "Special circumstance test failed for Fnr " + SPECIAL_FNR_3_JAN_73);

        assertFalse(Pid.isValidPid(SPECIAL_FNR_3_JAN_76, false), "Special circumstance test failed for Fnr " + SPECIAL_FNR_3_JAN_76);
        String specialFnr08Jun57 = "08065700000";
        assertFalse(Pid.isValidPid(specialFnr08Jun57, false), "Special circumstance test failed for Fnr " + specialFnr08Jun57);
    }

    @Test
    void should_validate_true_when_special_circumstances_allowed_and_pid_has_special_circumstances() {
        // validate the Pids with special circumstances enabled
        assertTrue(Pid.isValidPid(SPECIAL_FNR_0, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_0);
        assertTrue(Pid.isValidPid(SPECIAL_FNR_1, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_1);

        assertTrue(Pid.isValidPid(SPECIAL_FNR_29_FEB, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_29_FEB);

        assertTrue(Pid.isValidPid(SPECIAL_FNR_61_MAY, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_61_MAY);
        assertTrue(Pid.isValidPid(SPECIAL_FNR_21_XXX, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_21_XXX);

        assertTrue(Pid.isValidPid(SPECIAL_FNR_3_JAN_73, true), "Special circumstance test failed for Fnr " + SPECIAL_FNR_3_JAN_73);
        assertTrue(Pid.isValidPid(SPECIAL_FNR_3_JAN_76, true), "Special circumstance test failed for Fnr " + SPECIAL_FNR_3_JAN_76);
    }

    @Test
    void should_validate_false_when_pids_with_unallowed_special_circumstances() {
        assertFalse(Pid.isValidPid(SPECIAL_FNR_ZERO, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_ZERO);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_30_FEB, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_30_FEB);
        assertFalse(Pid.isValidPid(SPECIAL_FNR_2, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_2);
    }

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
    void should_create_Pid_when_valid_fnr_as_input() {
        assertTrue(Pid.isValidPid(TestFnrs.NORMAL), "Could not create Pid using valid Fnr");
    }

    @Test
    void should_create_Pid_when_valid_bostnr_as_input() {
        String monthBelow10BostNrWs = "04250100286";
        assertTrue(Pid.isValidPid(MONTH_ABOVE_9_BOST_NR), "Could not create fnr using a Bostnummer with birth month > 9");
        assertTrue(Pid.isValidPid(monthBelow10BostNrWs), "Could not create fnr using a Bostnummer with birth month < 10");
    }

    @Test
    void should_create_Pid_when_valid_dnummer_as_input() {
        String dnummerValid0 = "41060094231";
        assertTrue(Pid.isValidPid(dnummerValid0), "Could not create fnr using a valid Dnummer with day > 40:" + dnummerValid0);
    }

    @Test
    void should_validate_true_when_isDnummer_check_on_dnummer() {
        String dnummerValid1 = "61080098013";
        assertTrue(new Pid(dnummerValid1).isDnummer(), "Failed check of valid dnummer:" + dnummerValid1);
    }

    @Test
    void should_validate_false_when_isDnummer_check_on_fnr() {
        assertFalse(new Pid(TestFnrs.NORMAL).isDnummer(), "Failed check of dnummer with normal fnr:" + TestFnrs.NORMAL);
    }

    @Test
    void should_validate_true_when_isDummer_check_on_dnummer_with_special_circumstances() {
        String dnummerInvalidUnlessAcceptSpecialCircumstances = "41018100000";
        assertTrue(new Pid(dnummerInvalidUnlessAcceptSpecialCircumstances, true).isDnummer(), "Failed check of special dnummer:" + dnummerInvalidUnlessAcceptSpecialCircumstances);
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

    @Test
    void should_validate_true_when_dnummer_ending_with_00000N_and_special_circumstances_false() {
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_1, false), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_2, false), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_3, false), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_4, false), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_5, false), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_6, false), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_7, false), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_8, false), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_9, false), "Could not create Dnr");
    }

    @Test
    void should_validate_true_when_dnummer_end_with_00000N_and_special_circumstances_true() {
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_1, true), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_2, true), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_3, true), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_4, true), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_5, true), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_6, true), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_7, true), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_8, true), "Could not create Dnr");
        assertTrue(Pid.isValidPid(D_NUMMER_VALID_LOW_PNR_9, true), "Could not create Dnr");
    }
}
