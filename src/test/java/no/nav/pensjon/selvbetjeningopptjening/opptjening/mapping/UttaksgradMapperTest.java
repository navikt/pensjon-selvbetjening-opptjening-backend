package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.UttaksgradDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UttaksgradMapperTest {

    @Test
    void test_that_fromDto_maps_relevant_values() {
        var dto = new UttaksgradDto();
        dto.setFomDato(LocalDate.of(1991, 2, 4));
        dto.setTomDato(LocalDate.of(1992, 3, 5));
        dto.setUttaksgrad(11);
        dto.setVedtakId(1L);

        List<Uttaksgrad> uttaksgrader = UttaksgradMapper.fromDtos(List.of(dto));

        Uttaksgrad uttaksgrad = uttaksgrader.get(0);
        assertEquals(11, uttaksgrad.getUttaksgrad());
        assertEquals(1L, uttaksgrad.getVedtakId());
        assertEquals(LocalDate.of(1991, 2, 4), uttaksgrad.getFomDate());
        assertEquals(LocalDate.of(1992, 3, 5), uttaksgrad.getTomDate());
    }
}
