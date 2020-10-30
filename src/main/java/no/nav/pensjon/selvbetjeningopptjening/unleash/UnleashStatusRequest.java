package no.nav.pensjon.selvbetjeningopptjening.unleash;

import java.util.List;

public class UnleashStatusRequest {
    private List<String> toggleList;

    public List<String> getToggleList() {
        return toggleList;
    }
    public void setToggleList(List<String> toggleList) {
        this.toggleList = toggleList;
    }
}
