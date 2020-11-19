package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.InntektDto;
import no.nav.pensjon.selvbetjeningopptjening.model.OmsorgDto;
import no.nav.pensjon.selvbetjeningopptjening.model.PensjonspoengDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class PensjonspoengMapper {

    public static List<Pensjonspoeng> fromDto(List<PensjonspoengDto> list) {
        return list == null ? null
                :
                list.stream()
                        .map(PensjonspoengMapper::fromDto)
                        .collect(toList());
    }

    private static Pensjonspoeng fromDto(PensjonspoengDto dto) {
        return dto == null ? null
                :
                new Pensjonspoeng(
                        dto.getAr(),
                        dto.getPensjonspoengType(),
                        dto.getPoeng(),
                        fromDto(dto.getInntekt()),
                        fromDto(dto.getOmsorg()));
    }

    private static Inntekt fromDto(InntektDto dto) {
        return dto == null ? null
                :
                new Inntekt(
                        dto.getInntektAr(),
                        dto.getInntektType(),
                        dto.getBelop());
    }

    private static Omsorg fromDto(OmsorgDto dto) {
        return dto == null ? null
                :
                new Omsorg(dto.getOmsorgType());
    }
}
