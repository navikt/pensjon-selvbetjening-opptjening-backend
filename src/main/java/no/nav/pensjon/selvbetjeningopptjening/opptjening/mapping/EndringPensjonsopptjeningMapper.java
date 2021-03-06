package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.EndringPensjonsopptjening;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.EndringPensjonsopptjeningDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

class EndringPensjonsopptjeningMapper {

    static List<EndringPensjonsopptjeningDto> toDto(List<EndringPensjonsopptjening> list) {
        return list == null ? null
                : list
                .stream()
                .map(EndringPensjonsopptjeningMapper::toDto)
                .collect(toList());
    }

    private static EndringPensjonsopptjeningDto toDto(EndringPensjonsopptjening opptjening) {
        return opptjening == null ? null : newDto(opptjening);
    }

    private static EndringPensjonsopptjeningDto newDto(EndringPensjonsopptjening opptjening) {
        var dto = new EndringPensjonsopptjeningDto();
        dto.setArsakDetails(opptjening.getArsakDetails());
        dto.setArsakType(opptjening.getArsakType());
        dto.setDato(opptjening.getDato());
        dto.setEndringBelop(opptjening.getEndringsbelop());
        dto.setGrunnlag(opptjening.getGrunnlag());
        dto.setGrunnlagTypes(opptjening.getGrunnlagTypes());
        dto.setPensjonsbeholdningBelop(opptjening.getBeholdningsbelop());
        dto.setUttaksgrad(opptjening.getUttaksgrad());
        dto.setUforegrad(opptjening.getUforegrad());
        return dto;
    }
}
