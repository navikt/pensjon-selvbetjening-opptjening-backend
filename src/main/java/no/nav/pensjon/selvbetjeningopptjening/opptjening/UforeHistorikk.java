package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.util.List;

public class UforeHistorikk {

    private final List<Uforeperiode> uforeperioder;

    public UforeHistorikk(List<Uforeperiode> uforeperioder) {
        this.uforeperioder = uforeperioder;
    }

    public List<Uforeperiode> getUforeperioder() {
        return uforeperioder;
    }
}
