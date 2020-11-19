package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk;

public class UforeHistorikkMapper {

    public static UforeHistorikk fromDto(UforeHistorikkDto dto) {
        return dto == null ? null
                :
                new UforeHistorikk(dto.getUforeperiodeListe());
    }
}
