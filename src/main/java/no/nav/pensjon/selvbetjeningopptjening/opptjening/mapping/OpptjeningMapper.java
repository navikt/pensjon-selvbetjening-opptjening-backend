package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Opptjening;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningDto;

public class OpptjeningMapper {

    public static OpptjeningDto toDto(Opptjening opptjening) {
        return opptjening == null ? null : newDto(opptjening);
    }

    private static OpptjeningDto newDto(Opptjening opptjening) {
        var dto = new OpptjeningDto();
        dto.setOmsorgspoeng(opptjening.hasOmsorgspoeng() ? opptjening.getOmsorgspoeng() : null);
        dto.setPensjonspoeng(opptjening.hasPensjonspoeng() ? opptjening.getPensjonspoeng() : null);
        dto.setOmsorgspoengType(opptjening.getOmsorgspoengType());
        dto.setMerknader(opptjening.getMerknader());
        dto.setPensjonsbeholdning(opptjening.hasPensjonsbeholdning() ? opptjening.getPensjonsbeholdning() : null);
        dto.setRestpensjon(opptjening.hasRestpensjon() ? opptjening.getRestpensjon() : null);
        dto.setMaksUforegrad(opptjening.getMaxUforegrad());
        dto.setPensjonsgivendeInntekt(opptjening.hasPensjonsgivendeInntekt() ? (int) opptjening.getPensjonsgivendeInntekt() : null);
        dto.setEndringOpptjening(EndringPensjonsopptjeningMapper.toDto(opptjening.getOpptjeningsendringer()));
        return dto;
    }
}
