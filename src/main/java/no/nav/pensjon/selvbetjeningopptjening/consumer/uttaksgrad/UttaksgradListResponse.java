package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

public class UttaksgradListResponse {
    private List<Uttaksgrad> uttaksgradList;

    public List<Uttaksgrad> getUttaksgradList() {
        return uttaksgradList;
    }

    public void setUttaksgradList(List<Uttaksgrad> uttaksgradList) {
        this.uttaksgradList = uttaksgradList;
    }
}
