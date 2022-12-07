package no.nav.pensjon.selvbetjeningopptjening.fullmakt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.Aktoertype;

import java.util.List;

public record AktoerDto(
        @JsonProperty("aktorNr") String aktoernummer,
        @JsonProperty("kodeAktorType") String type,
        @JsonProperty("fullmaktFra") List<FullmaktDto> fullmakterFra,
        @JsonProperty("fullmaktTil") List<FullmaktDto> fullmakterTil) {

    public boolean isPerson(String personId) {
        return aktoernummer.equals(personId)
                && Aktoertype.PERSON.name().equals(type);
    }
}
