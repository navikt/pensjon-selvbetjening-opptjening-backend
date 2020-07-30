package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Sak;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Uforeperiode;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

public class PersonConsumer {
    private String endpoint;

    public PersonConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public LocalDate getFodselsdato(String fnr) {
        //Returning dummy data for now
        return LocalDate.of(1955, 4, 8);
    }

    public List<Sak> getSakUttaksgradhistorikkForPerson(String fnr,
            String saktype) { //TODO: Saktype kan muligens bare alltid være satt til alder i stedet for parameter, men lar den stå inntil videre
        Sak sak1 = new Sak();
        sak1.setSakId("s1");
        sak1.setVedtakId(1L);

        Uttaksgrad uttaksgrad1 = new Uttaksgrad();
        uttaksgrad1.setFomDato(LocalDate.of(2020, 2, 1));
        uttaksgrad1.setTomDato(LocalDate.of(2021, 1, 1));
        uttaksgrad1.setUttaksgrad(60);

        Uttaksgrad uttaksgrad2 = new Uttaksgrad();
        uttaksgrad2.setFomDato(LocalDate.of(2018, 2, 1));
        uttaksgrad2.setTomDato(LocalDate.of(2019, 1, 1));
        uttaksgrad2.setUttaksgrad(20);
        sak1.setUttaksgradhistorikk(Arrays.asList(uttaksgrad1, uttaksgrad2));

        Sak sak2 = new Sak();
        sak2.setSakId("s2");
        sak2.setVedtakId(2L);
        sak2.setUttaksgradhistorikk(Arrays.asList(uttaksgrad1, uttaksgrad2));

        return Arrays.asList(sak1, sak2);
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
        uforeperiode1.setUforetidspunktFom(LocalDate.of(2000, 1, 4));
        uforeperiode1.setUforetidspunktTom(LocalDate.of(2003, 1, 4));
        Uforeperiode uforeperiode2 = new Uforeperiode();
        uforeperiode2.setUforegrad(50);
        uforeperiode2.setUforetidspunktFom(LocalDate.of(2005, 1, 4));
        uforeperiode2.setUforetidspunktTom(LocalDate.of(2019, 1, 4));
        uforeHistorikk.setUforeperiodeListe(Arrays.asList(uforeperiode1, uforeperiode2));

        return uforeHistorikk;
    }
}
