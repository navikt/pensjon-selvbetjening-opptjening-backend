package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.OmsorgMapper.fromDtos;

public class BeholdningMapper {

    public static List<Beholdning> fromDto(List<BeholdningDto> list) {
        return list == null ? null
                :
                list.stream()
                        .map(BeholdningMapper::fromDto)
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
                        fromDto(dto.getLonnsvekstregulering()),
                        fromDto(dto.getInntektOpptjeningBelop()),
                        fromDto(dto.getOmsorgOpptjeningBelop()),
                        fromDto(dto.getDagpengerOpptjeningBelop()),
                        fromDto(dto.getForstegangstjenesteOpptjeningBelop()),
                        fromDto(dto.getUforeOpptjeningBelop()));
    }

    private static Lonnsvekstregulering fromDto(LonnsvekstreguleringDto dto) {
        return dto == null ? null
                :
                new Lonnsvekstregulering(dto.getReguleringsbelop(), dto.getReguleringsDato());
    }

    private static Inntektsopptjening fromDto(InntektOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Inntektsopptjening(
                        dto.getAr(),
                        dto.getBelop(),
                        InntektMapper.fromDto(dto.getSumPensjonsgivendeInntekt()));
    }

    private static Omsorgsopptjening fromDto(OmsorgOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Omsorgsopptjening(
                        dto.getAr(),
                        dto.getBelop(),
                        fromDtos(dto.getOmsorgListe()));
    }

    private static Dagpengeopptjening fromDto(DagpengerOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Dagpengeopptjening(
                        dto.getAr(),
                        dto.getBelopOrdinar(),
                        dto.getBelopFiskere());
    }

    private static Forstegangstjenesteopptjening fromDto(ForstegangstjenesteOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Forstegangstjenesteopptjening(
                        dto.getAr(),
                        dto.getBelop());
    }

    private static Uforeopptjening fromDto(UforeOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Uforeopptjening(
                        dto.getUforegrad(),
                        dto.getBelop());
    }
}
