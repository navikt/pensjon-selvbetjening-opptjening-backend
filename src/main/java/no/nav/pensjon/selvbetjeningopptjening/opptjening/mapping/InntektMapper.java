package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.InntektDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class InntektMapper {

    public static List<Inntekt> fromDto(List<InntektDto> list) {
        return list == null ? null
                :
                list.stream()
                        .map(InntektMapper::fromDto)
                        .collect(toList());
    }


    public static Inntekt fromDto(InntektDto dto) {
        return dto == null ? null
                :
                new Inntekt(
                        dto.getInntektAr(),
                        dto.getInntektType(),
                        dto.getBelop());
    }
}
