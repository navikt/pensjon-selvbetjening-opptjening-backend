package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.consumer.model.Restpensjon;

public class HentRestpensjonListeResponse {
    private List<Restpensjon> restpensjoner;

    public List<Restpensjon> getRestpensjoner() {
        return restpensjoner;
    }

    public void setRestpensjoner(List<Restpensjon> restpensjoner) {
        this.restpensjoner = restpensjoner;
    }
}
