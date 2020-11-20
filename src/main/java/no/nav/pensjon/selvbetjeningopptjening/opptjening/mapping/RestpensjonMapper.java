package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.RestpensjonDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class RestpensjonMapper {

    public static List<Restpensjon> fromDto(List<RestpensjonDto> dtos) {
        return dtos == null ? null
                :
                dtos.stream()
                        .map(RestpensjonMapper::fromDto)
                        .collect(toList());
    }

    private static Restpensjon fromDto(RestpensjonDto dto) {
        return dto == null ? null
                :
                new Restpensjon(
                        dto.getFomDato(),
                        dto.getRestGrunnpensjon(),
                        dto.getRestTilleggspensjon(),
                        dto.getRestPensjonstillegg());
    }
}
