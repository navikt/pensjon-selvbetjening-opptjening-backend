package no.nav.pensjon.selvbetjeningopptjening.util;

public final class Constants {

    public static final String NAV_CALL_ID = "Nav-Call-Id";
    public static final String ISSUER = "selvbetjening";
    public static final String POPP = "POPP";
    public static final String PEN = "PEN";

    /**
     * Birthyear of first group who has a right to new AFP
     */
    public static final int FIRST_BIRTHYEAR_WITH_NEW_AFP = 1948;

    /**
     * Birthyear of first group who has a right to new Alder
     */
    public static final int FIRST_BIRTHYEAR_WITH_NEW_ALDER = 1943;

    /**
     * The first birthyear that has "overgangsregler for opptjening"
     */
    public static final int FIRST_BIRTHYEAR_WITH_OVERGANGSREGLER = 1954;

    /**
     * The last birthyear that has "overgangsregler for opptjening"
     */
    public static final int LAST_BIRTHYEAR_WITH_OVERGANGSREGLER = 1962;

    public static final int REFORM_2010 = 2010;

    private Constants() {
    }
}
