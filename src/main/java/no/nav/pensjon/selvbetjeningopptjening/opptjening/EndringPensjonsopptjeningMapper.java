package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.util.List;
import java.util.stream.Collectors;

class EndringPensjonsopptjeningMapper {

    static List<EndringPensjonsopptjeningDto> toDto(List<EndringPensjonsopptjening> list) {
        return list
                .stream()
                .map(EndringPensjonsopptjeningMapper::toDto)
                .collect(Collectors.toList());
    }

    private static EndringPensjonsopptjeningDto toDto(EndringPensjonsopptjening opptjening) {
        EndringPensjonsopptjeningDto dto = new EndringPensjonsopptjeningDto();
        dto.setArsakDetails(opptjening.getArsakDetails());
        dto.setArsakType(opptjening.getArsakType());
        dto.setDato(opptjening.getDato());
        dto.setEndringBelop(opptjening.getEndringBelop());
        dto.setGrunnlag(opptjening.getGrunnlag());
        dto.setGrunnlagTypes(opptjening.getGrunnlagsTypes());
        dto.setPensjonsbeholdningBelop(opptjening.getPensjonsbeholdningBelop());
        dto.setUttaksgrad(opptjening.getUttaksgrad());
        return dto;
    }
}
