package no.nav.pensjon.selvbetjeningopptjening.person;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PidValidatorTest {

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
    private static final String TESTNORGE_FNR = "29885596930"; // +80 in "måned" value
    private static final String DOLLY_FNR = "28457848279"; // +40 in "måned" value

    @Test
    void should_validate_false_when_special_circumstances_not_allowed_and_pid_has_special_circumstances() {
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_0, false), "Special circumstance test without acceptspecialcircumstance set failed for Fnr" + SPECIAL_FNR_0);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_0), "Special circumstance test without acceptspecialcircumstance set failed for Fnr" + SPECIAL_FNR_0);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_1, false), "Special circumstance test without acceptspecialcircumstance set failed for Fnr" + SPECIAL_FNR_1);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_1), "Special circumstance test without acceptspecialcircumstance set failed for Fnr" + SPECIAL_FNR_1);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_2), "Special circumstance test failed for Fnr" + SPECIAL_FNR_2);

        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_29_FEB), "Special circumstance test failed for Fnr" + SPECIAL_FNR_29_FEB);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_29_FEB, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_29_FEB);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_30_FEB), "Special circumstance test failed for Fnr" + SPECIAL_FNR_30_FEB);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_30_FEB, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_29_FEB);

        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_61_MAY), "Special circumstance test failed for Fnr" + SPECIAL_FNR_61_MAY);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_61_MAY, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_61_MAY);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_21_XXX), "Special circumstance test failed for Fnr" + SPECIAL_FNR_21_XXX);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_21_XXX, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_21_XXX);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_ZERO), "Special circumstance test failed for Fnr" + SPECIAL_FNR_ZERO);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_ZERO, false), "Special circumstance test failed for Fnr" + SPECIAL_FNR_ZERO);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_3_JAN_73, false), "Special circumstance test failed for Fnr " + SPECIAL_FNR_3_JAN_73);

        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_3_JAN_76, false), "Special circumstance test failed for Fnr " + SPECIAL_FNR_3_JAN_76);
        String specialFnr08Jun57 = "08065700000";
        assertFalse(PidValidator.isValidPid(specialFnr08Jun57, false), "Special circumstance test failed for Fnr " + specialFnr08Jun57);
    }

    @Test
    void should_validate_true_when_special_circumstances_allowed_and_pid_has_special_circumstances() {
        assertTrue(PidValidator.isValidPid(SPECIAL_FNR_0, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_0);
        assertTrue(PidValidator.isValidPid(SPECIAL_FNR_1, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_1);

        assertTrue(PidValidator.isValidPid(SPECIAL_FNR_29_FEB, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_29_FEB);

        assertTrue(PidValidator.isValidPid(SPECIAL_FNR_61_MAY, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_61_MAY);
        assertTrue(PidValidator.isValidPid(SPECIAL_FNR_21_XXX, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_21_XXX);

        assertTrue(PidValidator.isValidPid(SPECIAL_FNR_3_JAN_73, true), "Special circumstance test failed for Fnr " + SPECIAL_FNR_3_JAN_73);
        assertTrue(PidValidator.isValidPid(SPECIAL_FNR_3_JAN_76, true), "Special circumstance test failed for Fnr " + SPECIAL_FNR_3_JAN_76);
    }

    @Test
    void should_validate_false_when_pids_with_unallowed_special_circumstances() {
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_ZERO, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_ZERO);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_30_FEB, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_30_FEB);
        assertFalse(PidValidator.isValidPid(SPECIAL_FNR_2, true), "Special circumstance test failed for Fnr" + SPECIAL_FNR_2);
    }

    @Test
    void should_create_Pid_when_valid_fnr_as_input() {
        assertTrue(PidValidator.isValidPid(TestFnrs.NORMAL), "Could not create Pid using valid Fnr");
    }

    @Test
    void should_create_Pid_when_valid_testnorgeFnr_as_input() {
        assertTrue(PidValidator.isValidPid(TESTNORGE_FNR), "Could not create Pid using valid Test-Norge Fnr");
    }

    @Test
    void should_create_Pid_when_valid_dollyFnr_as_input() {
        assertTrue(PidValidator.isValidPid(DOLLY_FNR), "Could not create PID from valid Dolly FNR");
    }

    @Test
    void should_create_Pid_when_valid_bostnr_as_input() {
        String monthBelow10BostNrWs = "04250100286";
        assertTrue(PidValidator.isValidPid(MONTH_ABOVE_9_BOST_NR), "Could not create fnr using a Bostnummer with birth month > 9");
        assertTrue(PidValidator.isValidPid(monthBelow10BostNrWs), "Could not create fnr using a Bostnummer with birth month < 10");
    }

    @Test
    void should_create_Pid_when_valid_dnummer_as_input() {
        String dnummerValid0 = "41060094231";
        assertTrue(PidValidator.isValidPid(dnummerValid0), "Could not create fnr using a valid Dnummer with day > 40:" + dnummerValid0);
    }

    @Test
    void should_validate_true_when_dnummer_ending_with_00000N_and_special_circumstances_false() {
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_1, false), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_2, false), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_3, false), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_4, false), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_5, false), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_6, false), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_7, false), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_8, false), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_9, false), "Could not create Dnr");
    }

    @Test
    void should_validate_true_when_dnummer_end_with_00000N_and_special_circumstances_true() {
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_1, true), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_2, true), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_3, true), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_4, true), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_5, true), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_6, true), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_7, true), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_8, true), "Could not create Dnr");
        assertTrue(PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_9, true), "Could not create Dnr");
    }

    @Test
    void getDatoPart_should_return_adjustedDatoPart_when_valid_testNorgeFnr_as_input() {
        assertEquals("29081955", PidValidator.getDatoPart(TESTNORGE_FNR));
    }

    @Test
    void getDatoPart_should_return_adjustedDatoPart_when_valid_dollyFnr_as_input() {
        assertEquals("28051978", PidValidator.getDatoPart(DOLLY_FNR));
    }
}
