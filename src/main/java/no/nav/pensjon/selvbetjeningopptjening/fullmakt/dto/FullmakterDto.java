package no.nav.pensjon.selvbetjeningopptjening.fullmakt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FullmakterDto(@JsonProperty("aktor") AktoerDto aktoer) {
}
