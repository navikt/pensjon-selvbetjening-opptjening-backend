package no.nav.pensjon.selvbetjeningopptjening.util;

public final class Constants {

    public static final String NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME";
    public static final String FNR_HEADER_VALUE = "fnr";
    public static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String NAV_CALL_ID = "Nav-Call-Id";
    public static final String NAV_USER_ID = "Nav-User-Id";
    public static final String NAV_AKTØR_ID = "Nav-Aktør-Id";
    public static final String ISSUER = "selvbetjening";
    public static final String NAV_TOKEN_EXPIRY_ID = "Nav-Token-Expiry";
    public static final String X_NAV_API_KEY = "x-nav-apiKey";
    public static final String FNR = "fnr";
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
