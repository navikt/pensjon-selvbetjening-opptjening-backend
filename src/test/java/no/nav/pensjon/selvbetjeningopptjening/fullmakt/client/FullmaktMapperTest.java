package no.nav.pensjon.selvbetjeningopptjening.fullmakt.client;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.Fullmakt;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.AktoerDto;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.FullmaktDto;
import no.nav.pensjon.selvbetjeningopptjening.mock.FullmaktDtoBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

class FullmaktMapperTest {

    @Test
    void fullmakter_maps_lack_of_fullmakter_as_empty_list() {
        assertEquals(0, FullmaktMapper.fullmakter(new AktoerDto(
                null, null, null, null)).size());

        assertEquals(0, FullmaktMapper.fullmakter(new AktoerDto(
                "", "", emptyList(), emptyList())).size());
    }

    @Test
    void fullmakter_maps_fullmaktFra() {
        List<FullmaktDto> fullmakterFra = List.of(fullmaktDto(1, 11));

        List<Fullmakt> fullmakter = FullmaktMapper.fullmakter(aktoerDto(fullmakterFra, emptyList()));

        assertEquals(1, fullmakter.size());
        Fullmakt fullmakt = fullmakter.get(0);
        assertEquals(1, fullmakt.getId());
        assertEquals(11, fullmakt.getVersjon());
    }

    @Test
    void fullmakter_maps_fullmaktTil() {
        List<FullmaktDto> fullmakterTil = List.of(fullmaktDto(2, 12));

        List<Fullmakt> fullmakter = FullmaktMapper.fullmakter(aktoerDto(emptyList(), fullmakterTil));

        assertEquals(1, fullmakter.size());
        Fullmakt fullmakt = fullmakter.get(0);
        assertEquals(2, fullmakt.getId());
        assertEquals(12, fullmakt.getVersjon());
    }

    @Test
    void fullmakter_maps_both_fullmakterFra_and_fullmakterTil() {
        List<FullmaktDto> fra = List.of(fullmaktDto(1, 11), fullmaktDto(3, 13));
        List<FullmaktDto> til = List.of(fullmaktDto(2, 12), fullmaktDto(4, 14));

        List<Fullmakt> fullmakter = FullmaktMapper.fullmakter(aktoerDto(fra, til));

        assertEquals(4, fullmakter.size());
        assertEquals(11, getFullmakt(fullmakter, 1).getVersjon());
        assertEquals(12, getFullmakt(fullmakter, 2).getVersjon());
        assertEquals(13, getFullmakt(fullmakter, 3).getVersjon());
        assertEquals(14, getFullmakt(fullmakter, 4).getVersjon());
    }

    private static AktoerDto aktoerDto(List<FullmaktDto> fra, List<FullmaktDto> til) {
        return new AktoerDto(
                "aktør1", "type1", fra, til);
    }

    private static FullmaktDto fullmaktDto(int id, int versjon) {
        return FullmaktDtoBuilder.instance(id).withVersjon(versjon).build();
    }

    private static Fullmakt getFullmakt(List<Fullmakt> fullmakter, int id) {
        return fullmakter
                .stream()
                .filter(fullmakt -> fullmakt.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
