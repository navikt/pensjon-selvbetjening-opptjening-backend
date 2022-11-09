package no.nav.pensjon.selvbetjeningopptjening.security.masking;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;

public class Masker {

    private static final int NORMAL_FNR_LENGTH = 11;
    private static final int END_INDEX_OF_BIRTH_DATE_PART_OF_FNR = 6;

    public static String maskFnr(Pid pid) {
        return pid == null ? "null" : maskFnr(pid.getPid());
    }

    public static String maskFnr(String fnr) {
        if (fnr == null) {
            return "null";
        }

        return fnr.length() == NORMAL_FNR_LENGTH
                ? fnr.substring(0, END_INDEX_OF_BIRTH_DATE_PART_OF_FNR) + "*****"
                : String.format("****** (length %d)", fnr.length());
    }
}
