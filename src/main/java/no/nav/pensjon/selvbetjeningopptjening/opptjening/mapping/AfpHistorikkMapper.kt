package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk;

public class AfpHistorikkMapper {

    public static AfpHistorikk fromDto(AfpHistorikkDto dto) {
        return dto == null ? null
                :
                new AfpHistorikk(
                        dto.getVirkFom(),
                        dto.getVirkTom());
    }
}
