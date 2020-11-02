package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException;

@ExtendWith(MockitoExtension.class)
public class PidTest {
    private String monthAbove9BostNr = "01327200336";
    private String normalFnr = "03029119367";
    private String specialFnr0 = "26067300000";
    private String specialFnr1 = "26067300001";
    private String specialFnr2 = "26067300002";
    private String specialFnr29Feb = "29027300000";
    private String specialFnr30Feb = "30027300000";
    private String specialFnr61May = "61057300000";
    private String specialFnr21XXX = "21257300000";
    private String specialFnrZero = "00000000000";
    private String specialFnr03Jan76 = "03017600000";
    private String specialFnr03Jan73 = "03017300000"; // Passes the mod11 check, but not the special circumstance check
    private String dNummerValidLowPnr1 = "41015800001";
    private String dNummerValidLowPnr2 = "57022800002";
    private String dNummerValidLowPnr3 = "64025000003";
    private String dNummerValidLowPnr4 = "58044400004";
    private String dNummerValidLowPnr5 = "61073600005";
    private String dNummerValidLowPnr6 = "67045600006";
    private String dNummerValidLowPnr7 = "46073400007";
    private String dNummerValidLowPnr8 = "70103900008";
    private String dNummerValidLowPnr9 = "41033100009";

    @Test
    public void should_validate_false_when_special_circumstances_not_allowed_and_pid_has_special_circumstances() {
        assertFalse("Special circumstance test without acceptspecialcircumstance set failed for Fnr" + specialFnr0, Pid
                .isValidPid(specialFnr0, false));
        assertFalse("Special circumstance test without acceptspecialcircumstance set failed for Fnr" + specialFnr0, Pid
                .isValidPid(specialFnr0));
        assertFalse("Special circumstance test without acceptspecialcircumstance set failed for Fnr" + specialFnr1, Pid
                .isValidPid(specialFnr1, false));
        assertFalse("Special circumstance test without acceptspecialcircumstance set failed for Fnr" + specialFnr1, Pid
                .isValidPid(specialFnr1));
        assertFalse("Special circumstance test failed for Fnr" + specialFnr2, Pid.isValidPid(specialFnr2));

        assertFalse("Special circumstance test failed for Fnr" + specialFnr29Feb, Pid.isValidPid(specialFnr29Feb));
        assertFalse("Special circumstance test failed for Fnr" + specialFnr29Feb, Pid.isValidPid(specialFnr29Feb, false));
        assertFalse("Special circumstance test failed for Fnr" + specialFnr30Feb, Pid.isValidPid(specialFnr30Feb));
        assertFalse("Special circumstance test failed for Fnr" + specialFnr29Feb, Pid.isValidPid(specialFnr30Feb, false));

        assertFalse("Special circumstance test failed for Fnr" + specialFnr61May, Pid.isValidPid(specialFnr61May));
        assertFalse("Special circumstance test failed for Fnr" + specialFnr61May, Pid.isValidPid(specialFnr61May, false));
        assertFalse("Special circumstance test failed for Fnr" + specialFnr21XXX, Pid.isValidPid(specialFnr21XXX));
        assertFalse("Special circumstance test failed for Fnr" + specialFnr21XXX, Pid.isValidPid(specialFnr21XXX, false));
        assertFalse("Special circumstance test failed for Fnr" + specialFnrZero, Pid.isValidPid(specialFnrZero));
        assertFalse("Special circumstance test failed for Fnr" + specialFnrZero, Pid.isValidPid(specialFnrZero, false));
        assertFalse("Special circumstance test failed for Fnr " + specialFnr03Jan73, Pid.isValidPid(specialFnr03Jan73, false));

        assertFalse("Special circumstance test failed for Fnr " + specialFnr03Jan76, Pid.isValidPid(specialFnr03Jan76, false));
        String specialFnr08Jun57 = "08065700000";
        assertFalse("Special circumstance test failed for Fnr " + specialFnr08Jun57, Pid.isValidPid(specialFnr08Jun57, false));
    }

    @Test
    public void should_validate_true_when_special_circumstances_allowed_and_pid_has_special_circumstances() {
        // validate the Pids with special circumstances enabled
        assertTrue("Special circumstance test failed for Fnr" + specialFnr0, Pid.isValidPid(specialFnr0, true));
        assertTrue("Special circumstance test failed for Fnr" + specialFnr1, Pid.isValidPid(specialFnr1, true));

        assertTrue("Special circumstance test failed for Fnr" + specialFnr29Feb, Pid.isValidPid(specialFnr29Feb, true));

        assertTrue("Special circumstance test failed for Fnr" + specialFnr61May, Pid.isValidPid(specialFnr61May, true));
        assertTrue("Special circumstance test failed for Fnr" + specialFnr21XXX, Pid.isValidPid(specialFnr21XXX, true));

        assertTrue("Special circumstance test failed for Fnr " + specialFnr03Jan73, Pid.isValidPid(specialFnr03Jan73, true));
        assertTrue("Special circumstance test failed for Fnr " + specialFnr03Jan76, Pid.isValidPid(specialFnr03Jan76, true));
    }

    @Test
    public void should_validate_false_when_pids_with_unallowed_special_circumstances() {
        assertFalse("Special circumstance test failed for Fnr" + specialFnrZero, Pid.isValidPid(specialFnrZero, true));
        assertFalse("Special circumstance test failed for Fnr" + specialFnr30Feb, Pid.isValidPid(specialFnr30Feb, true));
        assertFalse("Special circumstance test failed for Fnr" + specialFnr2, Pid.isValidPid(specialFnr2, true));
    }

    @Test
    public void should_throw_PidValidationException_when_creating_Pid_with_invalid_inputs() {
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr0, false));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr1, false));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr2, true));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr2, false));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr29Feb, false));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr30Feb, true));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr30Feb, false));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr61May, false));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr21XXX, false));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnrZero, true));
        assertThrows(PidValidationException.class, () -> new Pid(specialFnrZero, false));
    }

    @Test
    public void should_create_Pid_when_valid_fnr_as_input() {
        assertTrue("Could not create Pid using valid Fnr", Pid.isValidPid(normalFnr));
    }

    @Test
    public void should_create_Pid_when_valid_bostnr_as_input() {
        String monthBelow10BostNrWs = "04250100286";

        assertTrue("Could not create fnr using a Bostnummer with birth month > 9", Pid.isValidPid(monthAbove9BostNr));
        assertTrue("Could not create fnr using a Bostnummer with birth month < 10", Pid.isValidPid(monthBelow10BostNrWs));
    }

    @Test
    public void should_create_Pid_when_valid_dnummer_as_input() {

        String dnummerValid0 = "41060094231";
        assertTrue("Could not create fnr using a valid Dnummer with day > 40:" + dnummerValid0, Pid.isValidPid(dnummerValid0));
    }

    @Test
    public void should_validate_true_when_isDnummer_check_on_dnummer() {
        String dnummerValid1 = "61080098013";
        assertTrue("Failed check of valid dnummer:" + dnummerValid1, new Pid(dnummerValid1).isDnummer());
    }

    @Test
    public void should_validate_false_when_isDnummer_check_on_fnr() {
        assertFalse("Failed check of dnummer with normal fnr:" + normalFnr, new Pid(normalFnr).isDnummer());
    }

    @Test
    public void should_validate_true_when_isDummer_check_on_dnummer_with_special_circumstances() {
        String dnummerInvalidUnlessAcceptSpecialCircumstances = "41018100000";
        assertTrue("Failed check of special dnummer:" + dnummerInvalidUnlessAcceptSpecialCircumstances, new Pid(
                dnummerInvalidUnlessAcceptSpecialCircumstances, true).isDnummer());
    }

    @Test
    public void should_extract_expected_date_when_getFodselsdato_on_valid_Pid_from_fnr() {
        LocalDate expectedDate = LocalDate.of(1991, Month.FEBRUARY, 3);
        assertThat(new Pid(normalFnr).getFodselsdato(), is(equalTo(expectedDate)));
    }

    @Test
    public void should_extract_expected_date_when_getFodselsdato_on_valid_Pid_from_bostnr() {
        LocalDate expectedDateForAbove = LocalDate.of(1972, Month.DECEMBER, 1);
        LocalDate expectedDateForBelow = LocalDate.of(1901, Month.MAY, 4);
        assertThat(new Pid(monthAbove9BostNr).getFodselsdato(), is(equalTo(expectedDateForAbove)));
        String monthBelow10BostNr = "04250100286";
        assertThat(new Pid(monthBelow10BostNr).getFodselsdato(), is(equalTo(expectedDateForBelow)));
    }

    @Test
    public void should_throwPidValidationException_when_getFodselsdato_on_valid_special_circumstance_Pid_that_lacks_fodselsdato() {

        assertThrows(PidValidationException.class, () -> new Pid(specialFnr0, true).getFodselsdato());
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr1, true).getFodselsdato());
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr29Feb, true).getFodselsdato());
        assertThrows(PidValidationException.class, () -> new Pid(specialFnr21XXX, true).getFodselsdato());
    }

    @Test
    public void should_getFodselsdato_when_Pid_of_type_specialFnr61May(){
        LocalDate expecteFodselsdato = LocalDate.of(1973, Month.MAY, 21);
        assertThat(new Pid(specialFnr61May, true).getFodselsdato(), is(expecteFodselsdato));
    }

    @Test
    public void should_return_fnr_when_Pid_getPid_and_normalFnr() {
        Pid pid = new Pid(normalFnr, false);
        assertThat(pid.toString(), is(equalTo(normalFnr)));
    }

    @Test
    public void should_validate_true_when_dnummer_ending_with_00000N_and_special_circumstances_false() {
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr1, false));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr2, false));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr3, false));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr4, false));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr5, false));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr6, false));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr7, false));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr8, false));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr9, false));
    }

    @Test
    public void should_validate_true_when_dnummer_end_with_00000N_and_special_circumstances_true() {
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr1, true));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr2, true));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr3, true));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr4, true));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr5, true));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr6, true));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr7, true));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr8, true));
        assertTrue("Could not create Dnr", Pid.isValidPid(dNummerValidLowPnr9, true));
    }
}