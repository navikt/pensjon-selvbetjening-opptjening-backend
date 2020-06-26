package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpptjeningsGrunnlag {

    private List<Inntekt> inntektListe;

    public List<Inntekt> getInntektListe() {
        return inntektListe;
    }

    public void setInntektListe(List<Inntekt> inntektListe) {
        this.inntektListe = inntektListe;
    }
}
