package no.nav.pensjon.selvbetjeningopptjening.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.springframework.format.annotation.DateTimeFormat;

import no.nav.pensjon.selvbetjeningopptjening.util.LocalDateTimeFromEpochDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Restpensjon {
    private String fnr;

    private Double restGrunnpensjon;

    private Double restTilleggspensjon;

    private Double restPensjonstillegg;

    private Long vedtakId;

    private LocalDate fomDato;

    private LocalDate tomDato;

    private List<Pensjonspoeng> pensjonspoengListe;

    private String oppdateringArsak;

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public Double getRestGrunnpensjon() {
        return restGrunnpensjon;
    }

    public void setRestGrunnpensjon(Double restGrunnpensjon) {
        this.restGrunnpensjon = restGrunnpensjon;
    }

    public Double getRestTilleggspensjon() {
        return restTilleggspensjon;
    }

    public void setRestTilleggspensjon(Double restTilleggspensjon) {
        this.restTilleggspensjon = restTilleggspensjon;
    }

    public Double getRestPensjonstillegg() {
        return restPensjonstillegg;
    }

    public void setRestPensjonstillegg(Double restPensjonstillegg) {
        this.restPensjonstillegg = restPensjonstillegg;
    }

    public Long getVedtakId() {
        return vedtakId;
    }

    public void setVedtakId(Long vedtakId) {
        this.vedtakId = vedtakId;
    }

    public LocalDate getFomDato() {
        return fomDato;
    }

    public void setFomDato(LocalDate fomDato) {
        this.fomDato = fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }

    public void setTomDato(LocalDate tomDato) {
        this.tomDato = tomDato;
    }

    public List<Pensjonspoeng> getPensjonspoengListe() {
        return pensjonspoengListe;
    }

    public void setPensjonspoengListe(List<Pensjonspoeng> pensjonspoengListe) {
        this.pensjonspoengListe = pensjonspoengListe;
    }

    public String getOppdateringArsak() {
        return oppdateringArsak;
    }

    public void setOppdateringArsak(String oppdateringArsak) {
        this.oppdateringArsak = oppdateringArsak;
    }
}
