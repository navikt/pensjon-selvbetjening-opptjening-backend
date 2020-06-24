package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.Restpensjon;

public class RestpensjonListeResponse {
    private List<Restpensjon> restpensjoner;

    public List<Restpensjon> getRestpensjoner() {
        return restpensjoner;
    }

    public void setRestpensjoner(List<Restpensjon> restpensjoner) {
        this.restpensjoner = restpensjoner;
    }
}
