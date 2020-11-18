package no.nav.pensjon.selvbetjeningopptjening.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LonnsvekstreguleringDto {

    private Long lonnsvekstreguleringId;
    private Double reguleringsbelop;
    private LocalDate reguleringsDato;

    public Long getLonnsvekstreguleringId() {
        return lonnsvekstreguleringId;
    }

    public void setLonnsvekstreguleringId(Long lonnsvekstreguleringId) {
        this.lonnsvekstreguleringId = lonnsvekstreguleringId;
    }

    public Double getReguleringsbelop() {
        return reguleringsbelop;
    }

    public void setReguleringsbelop(Double reguleringsbelop) {
        this.reguleringsbelop = reguleringsbelop;
    }

    public LocalDate getReguleringsDato() {
        return reguleringsDato;
    }

    public void setReguleringsDato(LocalDate reguleringsDato) {
        this.reguleringsDato = reguleringsDato;
    }
}
