package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.OmsorgMapper.fromDtos;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.OmsorgMapper.toDtos;

public class BeholdningMapper {

    public static List<Beholdning> fromDto(List<BeholdningDto> list) {
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
                        fromDto(dto.getLonnsvekstregulering()),
                        fromDto(dto.getInntektOpptjeningBelop()),
                        fromDto(dto.getOmsorgOpptjeningBelop()),
                        fromDto(dto.getDagpengerOpptjeningBelop()),
                        fromDto(dto.getForstegangstjenesteOpptjeningBelop()),
                        fromDto(dto.getUforeOpptjeningBelop()));
    }

    static BeholdningDto toDto(Beholdning beholdning) {
        return beholdning == null ? null : newDto(beholdning);
    }

    private static InntektOpptjeningBelopDto toDto(Inntektsopptjening opptjening) {
        return opptjening == null ? null : newDto(opptjening);
    }

    private static UforeOpptjeningBelopDto toDto(Uforeopptjening opptjening) {
        return opptjening == null ? null : newDto(opptjening);
    }

    private static ForstegangstjenesteOpptjeningBelopDto toDto(Forstegangstjenesteopptjening opptjening) {
        return opptjening == null ? null : newDto(opptjening);
    }

    private static DagpengerOpptjeningBelopDto toDto(Dagpengeopptjening opptjening) {
        return opptjening == null ? null : newDto(opptjening);
    }

    private static OmsorgOpptjeningBelopDto toDto(Omsorgsopptjening opptjening) {
        return opptjening == null ? null : newDto(opptjening);
    }

    private static LonnsvekstreguleringDto toDto(Lonnsvekstregulering regulering) {
        return regulering == null ? null : newDto(regulering);
    }

    private static BeholdningDto newDto(Beholdning beholdning) {
        var dto = new BeholdningDto();
        dto.setBeholdningId(beholdning.getId());
        dto.setFnr(beholdning.getFnr());
        dto.setStatus(beholdning.getStatus());
        dto.setBeholdningType(beholdning.getType());
        dto.setBelop(beholdning.getBelop());
        dto.setVedtakId(beholdning.getVedtakId());
        dto.setFomDato(beholdning.getFomDato());
        dto.setTomDato(beholdning.getTomDato());
        dto.setBeholdningGrunnlag(beholdning.getGrunnlag());
        dto.setBeholdningGrunnlagAvkortet(beholdning.getGrunnlagAvkortet());
        dto.setBeholdningInnskudd(beholdning.getInnskudd());
        dto.setBeholdningInnskuddUtenOmsorg(beholdning.getInnskuddUtenOmsorg());
        dto.setOppdateringArsak(beholdning.getOppdateringArsak());
        dto.setLonnsvekstregulering(toDto(beholdning.getLonnsvekstregulering()));
        dto.setInntektOpptjeningBelop(toDto(beholdning.getInntektsopptjening()));
        dto.setOmsorgOpptjeningBelop(toDto(beholdning.getOmsorgsopptjening()));
        dto.setDagpengerOpptjeningBelop(toDto(beholdning.getDagpengeopptjening()));
        dto.setForstegangstjenesteOpptjeningBelop(toDto(beholdning.getForstegangstjenesteopptjening()));
        dto.setUforeOpptjeningBelop(toDto(beholdning.getUforeopptjening()));
        return dto;
    }

    private static Lonnsvekstregulering fromDto(LonnsvekstreguleringDto dto) {
        return dto == null ? null
                :
                new Lonnsvekstregulering(dto.getReguleringsbelop());
    }

    private static LonnsvekstreguleringDto newDto(Lonnsvekstregulering domain) {
        var dto = new LonnsvekstreguleringDto();
        dto.setReguleringsbelop(domain.getBelop());
        return dto;
    }

    private static Inntektsopptjening fromDto(InntektOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Inntektsopptjening(
                        dto.getAr(),
                        dto.getBelop(),
                        InntektMapper.fromDto(dto.getSumPensjonsgivendeInntekt()));
    }

    private static InntektOpptjeningBelopDto newDto(Inntektsopptjening opptjening) {
        var dto = new InntektOpptjeningBelopDto();
        dto.setBelop(opptjening.getBelop());
        return dto;
    }

    private static Omsorgsopptjening fromDto(OmsorgOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Omsorgsopptjening(
                        dto.getAr(),
                        dto.getBelop(),
                        fromDtos(dto.getOmsorgListe()));
    }

    private static OmsorgOpptjeningBelopDto newDto(Omsorgsopptjening opptjening) {
        var dto = new OmsorgOpptjeningBelopDto();
        dto.setAr(opptjening.getYear());
        dto.setBelop(opptjening.getBelop());
        dto.setOmsorgListe(toDtos(opptjening.getOmsorger()));
        return dto;
    }

    private static Dagpengeopptjening fromDto(DagpengerOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Dagpengeopptjening(
                        dto.getAr(),
                        dto.getBelopOrdinar(),
                        dto.getBelopFiskere());
    }

    private static DagpengerOpptjeningBelopDto newDto(Dagpengeopptjening opptjening) {
        var dto = new DagpengerOpptjeningBelopDto();
        dto.setAr(opptjening.getYear());
        dto.setBelopOrdinar(opptjening.getOrdinartBelop());
        dto.setBelopFiskere(opptjening.getFiskerBelop());
        return dto;
    }

    private static Forstegangstjenesteopptjening fromDto(ForstegangstjenesteOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Forstegangstjenesteopptjening(
                        dto.getAr(),
                        dto.getBelop());
    }

    private static ForstegangstjenesteOpptjeningBelopDto newDto(Forstegangstjenesteopptjening opptjening) {
        var dto = new ForstegangstjenesteOpptjeningBelopDto();
        dto.setAr(opptjening.getYear());
        return dto;
    }

    private static Uforeopptjening fromDto(UforeOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Uforeopptjening(
                        dto.getAr(),
                        dto.getBelop());
    }

    private static UforeOpptjeningBelopDto newDto(Uforeopptjening opptjening) {
        var dto = new UforeOpptjeningBelopDto();
        dto.setUforegrad(opptjening.getUforegrad());
        dto.setBelop(opptjening.getBelop());
        return dto;
    }
}
