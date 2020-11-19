package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InntektOpptjeningBelopDto {

    private Long inntektOpptjeningBelopId;
    private Integer ar;
    private Double belop;
    private InntektDto sumPensjonsgivendeInntekt;
    private List<InntektDto> inntektListe = new ArrayList<>();

    public Long getInntektOpptjeningBelopId() {
        return inntektOpptjeningBelopId;
    }

    public void setInntektOpptjeningBelopId(Long inntektOpptjeningBelopId) {
        this.inntektOpptjeningBelopId = inntektOpptjeningBelopId;
    }

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

    public Double getBelop() {
        return belop;
    }

    public void setBelop(Double belop) {
        this.belop = belop;
    }

    public InntektDto getSumPensjonsgivendeInntekt() {
        return sumPensjonsgivendeInntekt;
    }

    public void setSumPensjonsgivendeInntekt(InntektDto sumPensjonsgivendeInntekt) {
        this.sumPensjonsgivendeInntekt = sumPensjonsgivendeInntekt;
    }

    public List<InntektDto> getInntektListe() {
        return inntektListe;
    }

    public void setInntektListe(List<InntektDto> inntektListe) {
        this.inntektListe = inntektListe;
    }
}
