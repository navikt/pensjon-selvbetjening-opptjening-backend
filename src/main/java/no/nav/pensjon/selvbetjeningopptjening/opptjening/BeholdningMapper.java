package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.BeholdningDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class BeholdningMapper {

    static List<Beholdning> fromDto(List<BeholdningDto> list) {
        return list == null ? null
                :
                list.stream()
                        .map(BeholdningMapper::fromDto)
                        .collect(toList());
    }

    public static List<BeholdningDto> toDto(List<Beholdning> list) {
        return list == null ? null
                :
                list.stream()
                        .map(BeholdningMapper::toDto)
                        .collect(toList());
    }

    private static Beholdning fromDto(BeholdningDto dto) {
        return dto == null ? null
                :
                new Beholdning(
                        dto.getBeholdningId(),
                        dto.getFnr(),
                        dto.getStatus(),
                        dto.getBeholdningType(),
                        dto.getBelop(),
                        dto.getVedtakId(),
                        dto.getFomDato(),
                        dto.getTomDato(),
                        dto.getBeholdningGrunnlag(),
                        dto.getBeholdningGrunnlagAvkortet(),
                        dto.getBeholdningInnskudd(),
                        dto.getBeholdningInnskuddUtenOmsorg(),
                        dto.getOppdateringArsak(),
                        dto.getLonnsvekstregulering(),
                        dto.getInntektOpptjeningBelop(),
                        dto.getOmsorgOpptjeningBelop(),
                        dto.getDagpengerOpptjeningBelop(),
                        dto.getForstegangstjenesteOpptjeningBelop(),
                        dto.getUforeOpptjeningBelop()
                );
    }

    private static BeholdningDto toDto(Beholdning domain) {
        if (domain == null) {
            return null;
        }

        var dto = new BeholdningDto();
        dto.setBeholdningId(domain.getId());
        dto.setFnr(domain.getFnr());
        dto.setStatus(domain.getStatus());
        dto.setBeholdningType(domain.getType());
        dto.setBelop(domain.getBelop());
        dto.setVedtakId(domain.getVedtakId());
        dto.setFomDato(domain.getFomDato());
        dto.setTomDato(domain.getTomDato());
        dto.setBeholdningGrunnlag(domain.getGrunnlag());
        dto.setBeholdningGrunnlagAvkortet(domain.getGrunnlagAvkortet());
        dto.setBeholdningInnskudd(domain.getInnskudd());
        dto.setBeholdningInnskuddUtenOmsorg(domain.getInnskuddUtenOmsorg());
        dto.setOppdateringArsak(domain.getOppdateringArsak());
        dto.setLonnsvekstregulering(domain.getLonnsvekstregulering());
        dto.setInntektOpptjeningBelop(domain.getInntektOpptjeningBelop());
        dto.setOmsorgOpptjeningBelop(domain.getOmsorgOpptjeningBelop());
        dto.setDagpengerOpptjeningBelop(domain.getDagpengerOpptjeningBelop());
        dto.setForstegangstjenesteOpptjeningBelop(domain.getForstegangstjenesteOpptjeningBelop());
        dto.setUforeOpptjeningBelop(domain.getUforeOpptjeningBelop());
        return dto;
    }
}
