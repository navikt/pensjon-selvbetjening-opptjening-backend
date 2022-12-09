package no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto;

import java.util.List;

/**
 * Field names and types are dictated by fullmakt API.
 */
public record AktoerDto(
        String aktorNr,
        String kodeAktorType,
        List<FullmaktDto> fullmaktFra,
        List<FullmaktDto> fullmaktTil) {
}
