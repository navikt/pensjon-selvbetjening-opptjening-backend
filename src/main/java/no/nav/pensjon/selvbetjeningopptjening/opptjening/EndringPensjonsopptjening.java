package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;

class EndringPensjonsopptjening {

    private final TypeArsakCode arsakType;
    private final LocalDate dato;
    private final Double beholdningsbelop;
    private final Double endringsbelop;
    private final Integer uttaksgrad;
    private final Double grunnlag;
    private final List<DetailsArsakCode> arsakDetails;
    private final List<GrunnlagTypeCode> grunnlagTypes;
    private final Integer uforegrad;

    static EndringPensjonsopptjening inngaende(int year, double pensjonsbeholdningsbelop, Integer uttaksgrad) {
        List<DetailsArsakCode> arsakDetails = year == REFORM_2010 ? List.of(DetailsArsakCode.BEHOLDNING_2010) : null;

        return new EndringPensjonsopptjening(
                TypeArsakCode.INNGAENDE,
                lastDayOf(year - 1),
                pensjonsbeholdningsbelop,
                null,
                uttaksgrad,
                null,
                arsakDetails,
                null,
                null);
    }

    static EndringPensjonsopptjening nyOpptjening(int year,
                                                  double beholdningsbelop,
                                                  double innskudd,
                                                  Integer uttaksgrad,
                                                  double grunnlag,
                                                  List<GrunnlagTypeCode> grunnlagTypes,
                                                  Integer uforegrad) {
        TypeArsakCode opptjeningsarsak = year == REFORM_2010 ? TypeArsakCode.INNGAENDE_2010 : TypeArsakCode.OPPTJENING;

        return new EndringPensjonsopptjening(
                opptjeningsarsak,
                firstDayOf(year),
                beholdningsbelop,
                innskudd,
                uttaksgrad,
                grunnlag,
                getArsakDetails(year, uttaksgrad),
                grunnlagTypes,
                uforegrad);
    }

    static EndringPensjonsopptjening uttakAtStartOfYear(int year,
                                                        double beholdningsbelop,
                                                        double endringsbelop,
                                                        Integer uttaksgrad) {
        return new EndringPensjonsopptjening(
                TypeArsakCode.UTTAK,
                firstDayOf(year),
                beholdningsbelop,
                endringsbelop,
                uttaksgrad,
                null,
                List.of(DetailsArsakCode.UTTAK),
                null,
                null);
    }

    static EndringPensjonsopptjening uttak(LocalDate fomDate,
                                           double beholdningsbelop,
                                           double endringsbelop,
                                           Integer uttaksgrad) {
        return new EndringPensjonsopptjening(
                TypeArsakCode.UTTAK,
                fomDate,
                beholdningsbelop,
                endringsbelop,
                uttaksgrad,
                null,
                List.of(DetailsArsakCode.UTTAK),
                null,
                null);
    }

    static EndringPensjonsopptjening regulering(int year,
                                                double vedtakPensjonseringsbelop,
                                                double reguleringsbelop,
                                                Integer uttaksgrad) {
        return new EndringPensjonsopptjening(
                TypeArsakCode.REGULERING,
                reguleringDateOf(year),
                vedtakPensjonseringsbelop,
                reguleringsbelop,
                uttaksgrad,
                null,
                getReguleringsendringDetails(year),
                null,
                null);
    }

    static EndringPensjonsopptjening uttakOnReguleringDate(int year,
                                                           double beholdningsbelop,
                                                           double endringsbelop,
                                                           Integer uttaksgrad) {
        return new EndringPensjonsopptjening(
                TypeArsakCode.UTTAK,
                reguleringDateOf(year),
                beholdningsbelop,
                endringsbelop,
                uttaksgrad,
                null,
                List.of(DetailsArsakCode.UTTAK),
                null,
                null);
    }

    static EndringPensjonsopptjening utgaende(int year, double beholdningsbelop, Integer uttaksgrad) {
        return new EndringPensjonsopptjening(
                TypeArsakCode.UTGAENDE,
                lastDayOf(year),
                beholdningsbelop,
                null,
                uttaksgrad,
                null,
                null,
                null,
                null);
    }

    private EndringPensjonsopptjening(TypeArsakCode arsakType,
                                      LocalDate dato,
                                      Double beholdningsbelop,
                                      Double endringsbelop,
                                      Integer uttaksgrad,
                                      Double grunnlag,
                                      List<DetailsArsakCode> arsakDetails,
                                      List<GrunnlagTypeCode> grunnlagTypes,
                                      Integer uforegrad) {
        this.arsakType = arsakType;
        this.dato = dato;
        this.beholdningsbelop = beholdningsbelop;
        this.endringsbelop = endringsbelop;
        this.uttaksgrad = uttaksgrad;
        this.grunnlag = grunnlag;
        this.arsakDetails = arsakDetails == null ? new ArrayList<>() : arsakDetails;
        this.grunnlagTypes = grunnlagTypes;
        this.uforegrad = uforegrad;
    }

    TypeArsakCode getArsakType() {
        return arsakType;
    }

    LocalDate getDato() {
        return dato;
    }

    Double getEndringsbelop() {
        return endringsbelop;
    }

    Double getBeholdningsbelop() {
        return beholdningsbelop;
    }

    Integer getUttaksgrad() {
        return uttaksgrad;
    }

    Double getGrunnlag() {
        return grunnlag;
    }

    List<DetailsArsakCode> getArsakDetails() {
        return arsakDetails;
    }

    List<GrunnlagTypeCode> getGrunnlagTypes() {
        return grunnlagTypes;
    }

    Integer getUforegrad() { return uforegrad; }

    private static List<DetailsArsakCode> getArsakDetails(int year, Integer uttaksgrad) {
        List<DetailsArsakCode> details = new ArrayList<>();

        if (year == 2011) {
            details.add(DetailsArsakCode.OPPTJENING_2011);
        }

        if (uttaksgrad == null) {
            return details;
        }

        if (0 < uttaksgrad && uttaksgrad < 100) {
            details.add(DetailsArsakCode.OPPTJENING_GRADERT);
        } else if (uttaksgrad == 100) {
            details.add(DetailsArsakCode.OPPTJENING_HEL);
        } else if (year >= 2012) {
            details.add(DetailsArsakCode.OPPTJENING_2012);
        }

        return details;
    }

    private static List<DetailsArsakCode> getReguleringsendringDetails(int year) {
        List<DetailsArsakCode> details = new ArrayList<>();

        if (year == REFORM_2010) {
            details.add(DetailsArsakCode.REGULERING_2010);
        } else if (year > REFORM_2010) {
            details.add(DetailsArsakCode.REGULERING);
        }

        return details;
    }

    private static LocalDate firstDayOf(int year) {
        return LocalDate.of(year, Month.JANUARY, 1);
    }

    private static LocalDate reguleringDateOf(int year) {
        return LocalDate.of(year, Month.MAY, 1);
    }

    private static LocalDate lastDayOf(int year) {
        return LocalDate.of(year, Month.DECEMBER, 31);
    }
}
