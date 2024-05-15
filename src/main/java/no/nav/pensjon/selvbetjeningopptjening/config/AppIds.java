package no.nav.pensjon.selvbetjeningopptjening.config;

/**
 * Identities of applications with which the opptjening-backend integrates.
 */
public enum AppIds {

    PERSONDATALOSNINGEN("PDL", false, false),
    PENSJONSFAGLIG_KJERNE("PEN", false, false),
    PENSJONSOPPTJENING_REGISTER("POPP", false, false),
    FULLMAKT("pensjon-representasjon", true, false),
    SKJERMEDE_PERSONER_PIP("Skjermede-personer-PIP", false, false); // PIP = Policy information point

    public final String appName;
    public final boolean supportsTokenX;
    public final boolean supportsFullmakt;

    AppIds(String appName, boolean supportsTokenX, boolean supportsFullmakt) {
        this.appName = appName;
        this.supportsTokenX = supportsTokenX;
        this.supportsFullmakt = supportsFullmakt;
    }
}
