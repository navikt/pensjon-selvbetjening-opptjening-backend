package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.UttaksgradDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class UttaksgradMapper {

    public static List<Uttaksgrad> fromDtos(List<UttaksgradDto> dtos) {
        return dtos == null ? null
                :
                dtos.stream()
                        .map(UttaksgradMapper::fromDto)
                        .collect(toList());
    }

    private static Uttaksgrad fromDto(UttaksgradDto dto) {
        return dto == null ? null
                :
                new Uttaksgrad(
                        dto.getVedtakId(),
                        dto.getUttaksgrad(),
                        dto.getFomDato(),
                        dto.getTomDato());
    }
}
