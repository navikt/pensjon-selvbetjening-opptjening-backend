package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeperiodeDto;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uforeperiode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UforeHistorikkMapperTest {

    @Test
    void test_that_fromDto_maps_relevant_values() {
        var dto = new UforeHistorikkDto();
        dto.setUforeperiodeListe(List.of(uforeperiode()));

        UforeHistorikk historikk = UforeHistorikkMapper.fromDto(dto);

        List<Uforeperiode> perioder = historikk.getUforeperioder();
        assertEquals(1, perioder.size());
        assertEquals(11, perioder.get(0).getUforegrad());
    }

    private static UforeperiodeDto uforeperiode() {
        var periode = new UforeperiodeDto();
        periode.setUforegrad(11);
        periode.setUforetype(UforeTypeCode.UFORE);
        periode.setUfgFom(LocalDate.of(1991, 1, 1));
        periode.setUfgTom(LocalDate.of(1992, 12, 31));
        return periode;
    }
}
