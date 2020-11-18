package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.*;

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
                        fromDto(dto.getLonnsvekstregulering()),
                        fromDto(dto.getInntektOpptjeningBelop()),
                        fromDto(dto.getOmsorgOpptjeningBelop()),
                        fromDto(dto.getDagpengerOpptjeningBelop()),
                        fromDto(dto.getForstegangstjenesteOpptjeningBelop()),
                        fromDto(dto.getUforeOpptjeningBelop()));
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
        dto.setLonnsvekstregulering(toDto(domain.getLonnsvekstregulering()));
        dto.setInntektOpptjeningBelop(toDto(domain.getInntektsopptjening()));
        dto.setOmsorgOpptjeningBelop(toDto(domain.getOmsorgsopptjening()));
        dto.setDagpengerOpptjeningBelop(toDto(domain.getDagpengeopptjening()));
        dto.setForstegangstjenesteOpptjeningBelop(toDto(domain.getForstegangstjenesteopptjening()));
        dto.setUforeOpptjeningBelop(toDto(domain.getUforeopptjening()));
        return dto;
    }

    private static Lonnsvekstregulering fromDto(LonnsvekstreguleringDto dto) {
        return dto == null ? null
                :
                new Lonnsvekstregulering(dto.getReguleringsbelop());
    }

    private static LonnsvekstreguleringDto toDto(Lonnsvekstregulering domain) {
        if (domain == null) {
            return null;
        }

        var dto = new LonnsvekstreguleringDto();
        dto.setReguleringsbelop(domain.getBelop());
        return dto;
    }

    private static Inntektsopptjening fromDto(InntektOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Inntektsopptjening(dto.getBelop());
    }

    private static InntektOpptjeningBelopDto toDto(Inntektsopptjening domain) {
        if (domain == null) {
            return null;
        }

        var dto = new InntektOpptjeningBelopDto();
        dto.setBelop(domain.getBelop());
        return dto;
    }

    private static Omsorgsopptjening fromDto(OmsorgOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Omsorgsopptjening(
                        dto.getAr(),
                        dto.getBelop(),
                        fromOmsorgDtos(dto.getOmsorgListe()));
    }

    private static OmsorgOpptjeningBelopDto toDto(Omsorgsopptjening domain) {
        if (domain == null) {
            return null;
        }

        var dto = new OmsorgOpptjeningBelopDto();
        dto.setAr(domain.getYear());
        dto.setBelop(domain.getBelop());
        dto.setOmsorgListe(toOmsorgDtos(domain.getOmsorger()));
        return dto;
    }

    private static List<Omsorg> fromOmsorgDtos(List<OmsorgDto> dtos) {
        return dtos == null ? null
                :
                dtos.stream()
                        .map(BeholdningMapper::fromDto)
                        .collect(toList());
    }

    private static List<OmsorgDto> toOmsorgDtos(List<Omsorg> list) {
        return list == null ? null
                :
                list.stream()
                        .map(BeholdningMapper::toDto)
                        .collect(toList());
    }

    private static Omsorg fromDto(OmsorgDto dto) {
        return dto == null ? null
                :
                new Omsorg(dto.getOmsorgType());
    }

    private static OmsorgDto toDto(Omsorg domain) {
        if (domain == null) {
            return null;
        }

        var dto = new OmsorgDto();
        dto.setOmsorgType(domain.getType());
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

    private static DagpengerOpptjeningBelopDto toDto(Dagpengeopptjening domain) {
        if (domain == null) {
            return null;
        }

        var dto = new DagpengerOpptjeningBelopDto();
        dto.setAr(domain.getYear());
        dto.setBelopOrdinar(domain.getOrdinartBelop());
        dto.setBelopFiskere(domain.getFiskerBelop());
        return dto;
    }

    private static Forstegangstjenesteopptjening fromDto(ForstegangstjenesteOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Forstegangstjenesteopptjening(
                        dto.getAr(),
                        dto.getBelop());
    }

    private static ForstegangstjenesteOpptjeningBelopDto toDto(Forstegangstjenesteopptjening domain) {
        if (domain == null) {
            return null;
        }

        var dto = new ForstegangstjenesteOpptjeningBelopDto();
        dto.setAr(domain.getYear());
        return dto;
    }

    private static Uforeopptjening fromDto(UforeOpptjeningBelopDto dto) {
        return dto == null ? null
                :
                new Uforeopptjening(
                        dto.getAr(),
                        dto.getBelop());
    }

    private static UforeOpptjeningBelopDto toDto(Uforeopptjening domain) {
        if (domain == null) {
            return null;
        }

        var dto = new UforeOpptjeningBelopDto();
        dto.setUforegrad(domain.getUforegrad());
        dto.setBelop(domain.getBelop());
        return dto;
    }
}
