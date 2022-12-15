package no.nav.pensjon.selvbetjeningopptjening.config;

/**
 * Identities of applications with which the opptjening-backend integrates.
 */
public enum AppIds {

    PERSONDATALOSNINGEN("PDL", false),
    PENSJONSFAGLIG_KJERNE("PEN", false),
    PENSJONSOPPTJENING_REGISTER("POPP", false),
    FULLMAKT("pensjon-fullmakt", false),
    SKJERMEDE_PERSONER_PIP("Skjermede-personer-PIP", false); // PIP = Policy information point

    public final String appName;
    public final boolean supportsTokenX;

    AppIds(String appName, boolean supportsTokenX) {
        this.appName = appName;
        this.supportsTokenX = supportsTokenX;
    }
}
