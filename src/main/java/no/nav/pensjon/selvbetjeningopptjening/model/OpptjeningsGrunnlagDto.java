package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpptjeningsGrunnlagDto {

    private List<InntektDto> inntektListe;

    public List<InntektDto> getInntektListe() {
        return inntektListe;
    }

    public void setInntektListe(List<InntektDto> inntektListe) {
        this.inntektListe = inntektListe;
    }
}
