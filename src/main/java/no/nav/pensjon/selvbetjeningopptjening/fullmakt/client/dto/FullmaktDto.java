package no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;

/**
 * Field names and types are dictated by fullmakt API.
 */
public record FullmaktDto(
        AktoerDto aktorGir,
        AktoerDto aktorMottar,
        String fagomrade,
        Calendar fomDato,
        String kodeFullmaktNiva,
        String kodeFullmaktType,
        Integer versjon,
        String endretAv,
        Calendar endretDato,
        Integer fullmaktId,
        Boolean gyldig,
        String opprettetAv,
        Calendar opprettetDato,
        Calendar sistBrukt,
        Calendar tomDato) {
}
