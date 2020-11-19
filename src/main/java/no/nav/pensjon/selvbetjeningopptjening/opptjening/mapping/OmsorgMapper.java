package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.OmsorgDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Omsorg;

import java.util.List;

import static java.util.stream.Collectors.toList;

class OmsorgMapper {

    static List<Omsorg> fromDtos(List<OmsorgDto> dtos) {
        return dtos == null ? null
                :
                dtos.stream()
                        .map(OmsorgMapper::fromDto)
                        .collect(toList());
    }

    static List<OmsorgDto> toDtos(List<Omsorg> list) {
        return list == null ? null
                :
                list.stream()
                        .map(OmsorgMapper::toDto)
                        .collect(toList());
    }

    private static Omsorg fromDto(OmsorgDto dto) {
        return dto == null ? null
                :
                new Omsorg(dto.getOmsorgType());
    }

    private static OmsorgDto toDto(Omsorg omsorg) {
        return omsorg == null ? null : newDto(omsorg);
    }

    private static OmsorgDto newDto(Omsorg omsorg) {
        var dto = new OmsorgDto();
        dto.setOmsorgType(omsorg.getType());
        return dto;
    }
}
