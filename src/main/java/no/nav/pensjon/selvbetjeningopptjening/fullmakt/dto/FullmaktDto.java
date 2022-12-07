package no.nav.pensjon.selvbetjeningopptjening.fullmakt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;

public record FullmaktDto(
        @JsonProperty("aktorGir") AktoerDto aktoerGir,
        @JsonProperty("aktorMottar") AktoerDto aktoerMottar,
        @JsonProperty("fagomrade") String fagomraade,
        @JsonProperty("fomDato") Calendar fom,
        @JsonProperty("kodeFullmaktNiva") String nivaa,
        @JsonProperty("kodeFullmaktType") String type,
        Integer versjon,
        String endretAv,
        Calendar endretDato,
        @JsonProperty("fullmaktId") Integer id,
        Boolean gyldig,
        String opprettetAv,
        Calendar opprettetDato,
        Calendar sistBrukt,
        @JsonProperty("tomDato") Calendar tom) {
}
