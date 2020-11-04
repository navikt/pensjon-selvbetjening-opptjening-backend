package no.nav.pensjon.selvbetjeningopptjening.unleash;

import java.util.Map;

public class UnleashStatusResponse {
    private Map<String, Boolean> toggles;

    public Map<String, Boolean> getToggles() {
        return toggles;
    }

    public void setToggles(Map<String, Boolean> toggles) {
        this.toggles = toggles;
    }

}
