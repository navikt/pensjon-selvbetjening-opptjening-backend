package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeperiodeDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uforeperiode;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class UforeHistorikkMapper {

    public static UforeHistorikk fromDto(UforeHistorikkDto dto) {
        return dto == null ? null
                :
                new UforeHistorikk(fromDto(dto.getUforeperiodeListe()));
    }

    private static List<Uforeperiode> fromDto(List<UforeperiodeDto> list) {
        return list == null ? null
                :
                list.stream()
                        .map(UforeHistorikkMapper::fromDto)
                        .collect(toList());
    }

    private static Uforeperiode fromDto(UforeperiodeDto dto) {
        return dto == null ? null
                :
                new Uforeperiode(
                        dto.getUforegrad(),
                        dto.getUforetype(),
                        dto.getUfgFom(),
                        dto.getUfgFom());
    }
}
