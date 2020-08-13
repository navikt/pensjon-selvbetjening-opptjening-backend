package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import java.time.LocalDate;
import java.util.Arrays;

import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Uforeperiode;

public class PersonConsumer {
    private String endpoint;

    public PersonConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public AfpHistorikk getAfpHistorikkForPerson(String fnr) {
        //Returning dummy data for now
        AfpHistorikk afpHistorikk = new AfpHistorikk();
        afpHistorikk.setVirkFom(LocalDate.of(2019, 1, 4));
        afpHistorikk.setVirkTom(LocalDate.of(2020, 4, 19));

        return afpHistorikk;
    }

    public UforeHistorikk getUforeHistorikkForPerson(String fnr) {
        //Returning dummy data for now
        UforeHistorikk uforeHistorikk = new UforeHistorikk();
        Uforeperiode uforeperiode1 = new Uforeperiode();
        uforeperiode1.setUforegrad(70);
        uforeperiode1.setUfgFom(LocalDate.of(2000, 1, 4));
        uforeperiode1.setUfgTom(LocalDate.of(2003, 1, 4));
        Uforeperiode uforeperiode2 = new Uforeperiode();
        uforeperiode2.setUforegrad(50);
        uforeperiode2.setUfgFom(LocalDate.of(2005, 1, 4));
        uforeperiode2.setUfgTom(LocalDate.of(2019, 1, 4));
        uforeHistorikk.setUforeperiodeListe(Arrays.asList(uforeperiode1, uforeperiode2));

        return uforeHistorikk;
    }
}
