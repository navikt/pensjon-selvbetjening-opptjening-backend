package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.BeholdningDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class BeholdningMapper {

    static List<Beholdning> fromDto(List<BeholdningDto> list) {
        return list
                .stream()
                .map(BeholdningMapper::fromDto)
                .collect(toList());
    }

    public static List<BeholdningDto> toDto(List<Beholdning> list) {
        return list
                .stream()
                .map(BeholdningMapper::toDto)
                .collect(toList());
    }

    private static Beholdning fromDto(BeholdningDto dto) {
        return new Beholdning(
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
        var dto = new BeholdningDto();
        dto.setBeholdningId(domain.getBeholdningId());
        dto.setFnr(domain.getFnr());
        dto.setStatus(domain.getStatus());
        dto.setBeholdningType(domain.getBeholdningType());
        dto.setBelop(domain.getBelop());
        dto.setVedtakId(domain.getVedtakId());
        dto.setFomDato(domain.getFomDato());
        dto.setTomDato(domain.getTomDato());
        dto.setBeholdningGrunnlag(domain.getBeholdningGrunnlag());
        dto.setBeholdningGrunnlagAvkortet(domain.getBeholdningGrunnlagAvkortet());
        dto.setBeholdningInnskudd(domain.getBeholdningInnskudd());
        dto.setBeholdningInnskuddUtenOmsorg(domain.getBeholdningInnskuddUtenOmsorg());
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
