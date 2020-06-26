package no.nav.pensjon.selvbetjeningopptjening.consumer.sak;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

public class SakConsumer {
    String endpoint;

    public SakConsumer(String endpoint){
        this.endpoint = endpoint;
    }

    public List<Uttaksgrad> getUttaksgradhistorikkForSak(String sakId){
        //Returning dummy data for now
        Uttaksgrad uttaksgrad1 = new Uttaksgrad();
        uttaksgrad1.setFomDato(LocalDate.of(2020,2,1));
        uttaksgrad1.setTomDato(LocalDate.of(2021, 1,1));
        uttaksgrad1.setUttaksgrad(60);

        Uttaksgrad uttaksgrad2 = new Uttaksgrad();
        uttaksgrad2.setFomDato(LocalDate.of(2018,2,1));
        uttaksgrad2.setTomDato(LocalDate.of(2019, 1,1));
        uttaksgrad2.setUttaksgrad(20);
        return Arrays.asList(uttaksgrad1, uttaksgrad2);
    }
}
