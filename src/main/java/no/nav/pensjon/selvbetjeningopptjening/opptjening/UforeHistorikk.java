package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.Uforeperiode;

import java.util.List;

public class UforeHistorikk {

    private List<Uforeperiode> uforeperioder;

    public UforeHistorikk(List<Uforeperiode> uforeperioder) {
        this.uforeperioder = uforeperioder;
    }

    public List<Uforeperiode> getUforeperioder() {
        return uforeperioder;
    }
}
