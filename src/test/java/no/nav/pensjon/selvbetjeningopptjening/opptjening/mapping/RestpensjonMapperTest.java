package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.PensjonspoengDto;
import no.nav.pensjon.selvbetjeningopptjening.model.RestpensjonDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestpensjonMapperTest {

    @Test
    void test_that_fromDto_maps_relevant_values() {
        var dto = new RestpensjonDto();
        dto.setFnr("12345678901");
        dto.setFomDato(LocalDate.of(1991, 2, 4));
        dto.setTomDato(LocalDate.of(1992, 3, 5));
        dto.setRestGrunnpensjon(1.1D);
        dto.setRestTilleggspensjon(2.1D);
        dto.setRestPensjonstillegg(3.1D);
        dto.setVedtakId(1L);
        dto.setOppdateringArsak("årsak");
        dto.setPensjonspoengListe(List.of(new PensjonspoengDto()));

        List<Restpensjon> pensjoner = RestpensjonMapper.fromDto(List.of(dto));

        Restpensjon uttaksgrad = pensjoner.get(0);
        assertEquals(1.1D, uttaksgrad.getRestGrunnpensjon());
        assertEquals(2.1D, uttaksgrad.getRestTilleggspensjon());
        assertEquals(3.1D, uttaksgrad.getRestPensjonstillegg());
        assertEquals(LocalDate.of(1991, 2, 4), uttaksgrad.getFomDate());
        // Note: No mapping for:
        // - fnr
        // - tomDato
        // - vedtakId
        // - oppdateringArsak
        // - pensjonspoengListe
    }

    @Test
    void test_that_fromDto_replaces_null_amounts_by_zero() {
        List<Restpensjon> pensjoner = RestpensjonMapper.fromDto(List.of(new RestpensjonDto()));

        Restpensjon uttaksgrad = pensjoner.get(0);
        assertEquals(0D, uttaksgrad.getRestGrunnpensjon());
        assertEquals(0D, uttaksgrad.getRestTilleggspensjon());
        assertEquals(0D, uttaksgrad.getRestPensjonstillegg());
    }
}
