package no.nav.pensjon.selvbetjeningopptjening.config;

/**
 * Identities of applications with which the opptjening-backend integrates.
 */
public enum AppIds {

    PERSONDATALOSNINGEN("PDL", false),
    PENSJONSFAGLIG_KJERNE("PEN", false),
    PENSJONSOPPTJENING_REGISTER("POPP", false),
    SKJERMEDE_PERSONER_PIP("Skjermede-personer-PIP", false); // PIP = Policy information point

    public final String appName;
    public final boolean useClientCredentials;

    AppIds(String appName, boolean useClientCredentials) {
        this.appName = appName;
        this.useClientCredentials = useClientCredentials;
    }
}
