package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.UttaksgradDto;

public class UttaksgradListResponse {

    private List<UttaksgradDto> uttaksgradList;

    public List<UttaksgradDto> getUttaksgradList() {
        return uttaksgradList;
    }

    public void setUttaksgradList(List<UttaksgradDto> uttaksgradList) {
        this.uttaksgradList = uttaksgradList;
    }
}
