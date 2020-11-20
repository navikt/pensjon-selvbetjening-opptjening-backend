package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.RestpensjonDto;

public class RestpensjonListeResponse {

    private List<RestpensjonDto> restpensjoner;

    public List<RestpensjonDto> getRestpensjoner() {
        return restpensjoner;
    }

    public void setRestpensjoner(List<RestpensjonDto> restpensjoner) {
        this.restpensjoner = restpensjoner;
    }
}
