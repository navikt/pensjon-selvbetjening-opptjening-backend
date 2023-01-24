package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import java.util.List;

/**
 * Aktør (fullmaktsgiver or fullmektig).
 */
public record Aktoer(
        String aktoernummer,
        String type,
        List<Fullmakt> fullmakterFra,
        List<Fullmakt> fullmakterTil) {

    public boolean isPerson(String personId) {
        return aktoernummer.equals(personId)
                && Aktoertype.PERSON.name().equals(type);
    }

    public boolean isSamhandler() {
        return Aktoertype.SAMHANDLER.name().equals(type);
    }
}
