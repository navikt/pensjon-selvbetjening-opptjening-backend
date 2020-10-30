package no.nav.pensjon.selvbetjeningopptjening.unleash;

import java.util.Map;

public class UnleashStatusResponse {
    private Map<String, Boolean> unleash;

    public Map<String, Boolean> getUnleash() {
        return unleash;
    }

    public void setUnleash(Map<String, Boolean> unleash) {
        this.unleash = unleash;
    }

}
