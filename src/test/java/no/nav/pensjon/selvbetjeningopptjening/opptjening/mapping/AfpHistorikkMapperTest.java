package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AfpHistorikkMapperTest {

    @Test
    void test_that_fromDto_maps_relevant_values() {
        var dto = new AfpHistorikkDto();
        dto.setVirkFom(LocalDate.of(1991, 2, 4));
        dto.setVirkTom(LocalDate.of(1992, 3, 5));

        AfpHistorikk historikk = AfpHistorikkMapper.fromDto(dto);

        assertEquals(LocalDate.of(1991, 2, 4), historikk.getVirkningFomDate());
        assertEquals(1991, historikk.getStartYear());
        assertEquals(1992, historikk.getEndYearOrDefault(() -> null));
    }

    @Test
    void test_that_defaultValue_used_when_no_endYear() {
        var dto = new AfpHistorikkDto();
        dto.setVirkFom(LocalDate.MIN);
        dto.setVirkTom(null); // hence no end year

        AfpHistorikk beholdninger = AfpHistorikkMapper.fromDto(dto);

        assertEquals(1992, beholdninger.getEndYearOrDefault(() -> 1992));
    }
}
