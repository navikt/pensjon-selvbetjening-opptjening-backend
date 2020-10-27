package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.BeholdningDto;
import no.nav.pensjon.selvbetjeningopptjening.model.Lonnsvekstregulering;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.nav.pensjon.selvbetjeningopptjening.util.BeholdningUtil.isBeholdningWithinPeriode;
import static no.nav.pensjon.selvbetjeningopptjening.util.BeholdningUtil.sortBeholdningerByDate;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.isDateInPeriod;

public class EndringPensjonsbeholdningCalculator {

    private LocalDate lastDayOfPreviousYear;
    private LocalDate firstDayOfGivenYear;
    private LocalDate dayBeforeReguleringGivenYear;
    private LocalDate reguleringDayGivenYear;
    private LocalDate lastDayOfGivenYear;

    List<EndringPensjonsopptjeningDto> calculateEndringPensjonsbeholdning(int year, List<BeholdningDto> beholdninger, List<Uttaksgrad> uttaksgrader) {
        List<Beholdning> sortedBeholdninger = sortBeholdningerByDate(BeholdningMapper.fromDto(beholdninger));

        firstDayOfGivenYear = LocalDate.of(year, Month.JANUARY, 1);
        dayBeforeReguleringGivenYear = LocalDate.of(year, Month.APRIL, 30);
        reguleringDayGivenYear = LocalDate.of(year, Month.MAY, 1);
        lastDayOfGivenYear = LocalDate.of(year, Month.DECEMBER, 31);
        lastDayOfPreviousYear = LocalDate.of(year - 1, Month.DECEMBER, 31);

        return sortedBeholdninger.isEmpty()
                ? null
                : EndringPensjonsopptjeningMapper.toDto(createPensjonsbeholdningEndringListe(sortedBeholdninger, year, uttaksgrader));
    }

    private List<EndringPensjonsopptjening> createPensjonsbeholdningEndringListe(List<Beholdning> sortedBeholdninger, Integer year, List<Uttaksgrad> uttaksgrader) {
        List<EndringPensjonsopptjening> beholdningsendringer = new ArrayList<>();

        Beholdning forrigeBeholdning = addInngaendeBeholdning(sortedBeholdninger, beholdningsendringer, uttaksgrader, year);
        forrigeBeholdning = addNyOpptjening(sortedBeholdninger, beholdningsendringer, forrigeBeholdning, year, uttaksgrader);
        forrigeBeholdning = addChangesUttaksgradBeforeRegulering(sortedBeholdninger, beholdningsendringer, forrigeBeholdning, uttaksgrader);
        forrigeBeholdning = addRegulering(sortedBeholdninger, beholdningsendringer, forrigeBeholdning, uttaksgrader, year);
        addChangesUttaksgradAfterReguleringAtMay1th(sortedBeholdninger, beholdningsendringer, forrigeBeholdning, uttaksgrader);
        addUtgaendeBeholdning(sortedBeholdninger, beholdningsendringer, uttaksgrader);

        return beholdningsendringer;
    }

    private Beholdning addInngaendeBeholdning(List<Beholdning> beholdninger, List<EndringPensjonsopptjening> beholdningsendringer, List<Uttaksgrad> uttaksgrader, Integer year) {
        Double pensjonsbeholdningBelop = 0D;
        Beholdning beholdningUsed = null;
        Integer uttaksgrad = 0;

        for (Beholdning beholdning : beholdninger) {
            if (beholdning.getTomDato() != null && beholdning.getTomDato().isEqual(lastDayOfPreviousYear)) {
                pensjonsbeholdningBelop = beholdning.getBelop();
                beholdningUsed = beholdning;
                uttaksgrad = fetchUttaksgrad(lastDayOfPreviousYear, beholdningUsed, uttaksgrader);
            }
        }

        var endring = new EndringPensjonsopptjening(TypeArsakCode.INNGAENDE, lastDayOfPreviousYear, null, pensjonsbeholdningBelop, uttaksgrad, null);

        if (year == REFORM_2010) {
            endring.setArsakDetails(List.of(DetailsArsakCode.BEHOLDNING_2010));
        }

        beholdningsendringer.add(endring);
        return beholdningUsed;
    }

    private Integer fetchUttaksgrad(LocalDate date, Beholdning beholdning, List<Uttaksgrad> uttaksgrader) {
        if (beholdning == null || beholdning.getVedtakId() == null) {
            return 0;
        }

        Optional<Uttaksgrad> uttaksgradForBeholdning = uttaksgrader.stream()
                .filter(uttaksgrad -> uttaksgrad.getVedtakId().equals(beholdning.getVedtakId()))
                .filter(uttaksgrad -> isDateInPeriod(date, uttaksgrad.getFomDato(), uttaksgrad.getTomDato()))
                .findFirst();

        return uttaksgradForBeholdning.isPresent() ? uttaksgradForBeholdning.get().getUttaksgrad() : 0;
    }

    private Beholdning addNyOpptjening(List<Beholdning> beholdninger, List<EndringPensjonsopptjening> beholdningsendringer,
                                       Beholdning forrigeBeholdning, Integer year, List<Uttaksgrad> uttaksgrader) {
        Double inngaendeBelop = beholdningsendringer.get(0).getPensjonsbeholdningBelop();
        Beholdning beholdning = findBeholdningAtStartOfYear(beholdninger);

        if (beholdning == null) {
            return forrigeBeholdning;
        }

        Double innskudd = calculateInnskudd(year, beholdning);
        Double pensjonsbeholdningBelop = inngaendeBelop + innskudd;
        TypeArsakCode arsakTypeForNyOpptjening = year == REFORM_2010 ? TypeArsakCode.INNGAENDE_2010 : TypeArsakCode.OPPTJENING;

        if (isBeholdningWithinUttaksgradPeriodeIncludeSameDay(beholdning, uttaksgrader)) {
            addNyOpptjeningWhenBeholdningWithinUttaksgradPeriode(beholdningsendringer, year, uttaksgrader, beholdning, innskudd, pensjonsbeholdningBelop, arsakTypeForNyOpptjening);
        } else {
            addNyOpptjeningWhenBeholdningOutsideUttaksgradPeriode(beholdningsendringer, year, uttaksgrader, beholdning, innskudd, pensjonsbeholdningBelop, arsakTypeForNyOpptjening);
        }

        forrigeBeholdning = beholdning;
        return forrigeBeholdning;
    }

    private void addNyOpptjeningWhenBeholdningOutsideUttaksgradPeriode(List<EndringPensjonsopptjening> beholdningsendringer, Integer year, List<Uttaksgrad> uttaksgrader, Beholdning beholdning, Double innskudd, Double pensjonsbeholdningBelop, TypeArsakCode arsakTypeForNyOpptjening) {
        Integer uttaksgrad = fetchUttaksgrad(firstDayOfGivenYear, beholdning, uttaksgrader);

        EndringPensjonsopptjening endring =
                new EndringPensjonsopptjening(arsakTypeForNyOpptjening, firstDayOfGivenYear, innskudd,
                        pensjonsbeholdningBelop, uttaksgrad, beholdning.getBeholdningGrunnlag());

        endring.addDetailsToNyOpptjeningEndring(year);
        endring.setGrunnlagTypes(beholdning.getOpptjeningGrunnlagTypes());
        beholdningsendringer.add(endring);
    }

    private void addNyOpptjeningWhenBeholdningWithinUttaksgradPeriode(List<EndringPensjonsopptjening> beholdningsendringer,
                                                                      Integer givenYear,
                                                                      List<Uttaksgrad> uttaksgrader,
                                                                      Beholdning beholdning,
                                                                      Double innskudd,
                                                                      Double pensjonsbeholdningBelop,
                                                                      TypeArsakCode arsakTypeForNyOpptjening) {
        addNyOpptjeningWhenBeholdningOutsideUttaksgradPeriode(beholdningsendringer, givenYear, uttaksgrader, beholdning, innskudd, pensjonsbeholdningBelop, arsakTypeForNyOpptjening);

        var endringUttak = new EndringPensjonsopptjening(TypeArsakCode.UTTAK, firstDayOfGivenYear, beholdning.getBelop() - pensjonsbeholdningBelop,
                beholdning.getBelop(), fetchUttaksgrad(firstDayOfGivenYear, beholdning, uttaksgrader), null);

        endringUttak.setArsakDetails(List.of(DetailsArsakCode.UTTAK));
        beholdningsendringer.add(endringUttak);
    }

    /**
     * Det kan hende at ny opptjening ennå ikke har blitt oppdatert, men det eksisterer likevel en beholdning 01.01 hvis
     * har endret uttaksgrad.
     */
    private double calculateInnskudd(Integer givenYear, Beholdning beholdning) {
        if (beholdning.getBeholdningInnskudd() == null) {
            return 0D;
        }

        double innskudd = beholdning.getBeholdningInnskudd();
        Lonnsvekstregulering lonnsvekstregulering = beholdning.getLonnsvekstregulering();

        if (givenYear.equals(REFORM_2010) && lonnsvekstregulering != null
                && lonnsvekstregulering.getReguleringsbelop() != null) {
            innskudd += lonnsvekstregulering.getReguleringsbelop();
        }

        return innskudd;
    }

    private Beholdning findBeholdningAtStartOfYear(List<Beholdning> beholdninger) {
        return beholdninger
                .stream()
                .filter(beholdning -> beholdning.getFomDato().isEqual(firstDayOfGivenYear))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adding the changes on beholdning due to changes on uttaksgrad, between january the 1st and 31st of april, to the
     * PensjonsbeholdningEndring list, if uttaksgradchanges exists.
     *
     * @param beholdninger         the sorted beholdninger
     * @param beholdningsendringer the PensjonsbeholdningEndring
     * @param forrigeBeholdning    The previous beholdning
     * @return forrigeBeholdning
     */
    private Beholdning addChangesUttaksgradBeforeRegulering(List<Beholdning> beholdninger,
                                                            List<EndringPensjonsopptjening> beholdningsendringer, Beholdning forrigeBeholdning, List<Uttaksgrad> uttaksgrader) {
        Double previousBeholdningBelop = 0.0;

        for (Beholdning beholdning : beholdninger) {
            if (beholdning.getFomDato().isEqual(firstDayOfGivenYear)) {
                previousBeholdningBelop = beholdning.getBelop();
                forrigeBeholdning = beholdning;
            } else if (isBeholdningWithinPeriode(beholdning, firstDayOfGivenYear, reguleringDayGivenYear)) {
                EndringPensjonsopptjening endringUttak = new EndringPensjonsopptjening(TypeArsakCode.UTTAK, beholdning.getFomDato(),
                        beholdning.getBelop() - previousBeholdningBelop, beholdning.getBelop(), fetchUttaksgrad(beholdning.getFomDato(), beholdning, uttaksgrader), null);

                endringUttak.setArsakDetails(List.of(DetailsArsakCode.UTTAK));
                beholdningsendringer.add(endringUttak);

                previousBeholdningBelop = beholdning.getBelop();
                forrigeBeholdning = beholdning;
            }
        }
        return forrigeBeholdning;
    }

    private boolean isBeholdningWithinUttaksgradPeriodeIncludeSameDay(Beholdning beholdning, List<Uttaksgrad> uttaksgrader) {
        return uttaksgrader.stream().anyMatch(uttaksgrad ->
                beholdning.getFomDato().compareTo(uttaksgrad.getFomDato()) > -1
                        && (beholdning.getTomDato() == null && (uttaksgrad.getTomDato() == null || uttaksgrad.getTomDato().compareTo(beholdning.getFomDato()) > -1)
                        || (beholdning.getTomDato() != null && (uttaksgrad.getTomDato() == null || beholdning.getTomDato().compareTo(uttaksgrad.getTomDato()) < 1)))
        );
    }

    private Beholdning addRegulering(List<Beholdning> beholdninger, List<EndringPensjonsopptjening> beholdningsendringer,
                                     Beholdning forrigeBeholdning, List<Uttaksgrad> uttaksgrader, Integer givenYear) {
        Double tempBelop = beholdningsendringer.get(beholdningsendringer.size() - 1).getPensjonsbeholdningBelop();

        for (Beholdning beholdning : beholdninger) {
            if (beholdning.getFomDato().isEqual(reguleringDayGivenYear)) {
                LocalDate gyldigPaDato;
                Double vedtakPensjonseringsBelop = null;
                Lonnsvekstregulering lonnsvekstregulering = beholdning.getLonnsvekstregulering();

                if (lonnsvekstregulering != null && lonnsvekstregulering.getReguleringsbelop() != null) {
                    vedtakPensjonseringsBelop = lonnsvekstregulering.getReguleringsbelop() + tempBelop;

                    gyldigPaDato = beholdning.getBelop().equals(vedtakPensjonseringsBelop)
                            ? reguleringDayGivenYear
                            : dayBeforeReguleringGivenYear;

                    EndringPensjonsopptjening endringRegulering = new EndringPensjonsopptjening(TypeArsakCode.REGULERING, reguleringDayGivenYear, lonnsvekstregulering
                            .getReguleringsbelop(), vedtakPensjonseringsBelop, fetchUttaksgrad(gyldigPaDato, beholdning, uttaksgrader), null);

                    addDetailsToReguleringEndring(endringRegulering, givenYear);
                    beholdningsendringer.add(endringRegulering);

                    forrigeBeholdning = beholdning;
                }

                if (isBeholdningWithinUttaksgradPeriodeIncludeSameDay(beholdning, uttaksgrader)) {
                    Double belop = beholdning.getBelop();
                    if (vedtakPensjonseringsBelop != null) {
                        belop -= vedtakPensjonseringsBelop;
                    }
                    EndringPensjonsopptjening endringUttak = new EndringPensjonsopptjening(TypeArsakCode.UTTAK, reguleringDayGivenYear, belop, beholdning.getBelop(), fetchUttaksgrad(
                            reguleringDayGivenYear, beholdning, uttaksgrader), null);

                    endringUttak.setArsakDetails(List.of(DetailsArsakCode.UTTAK));
                    beholdningsendringer.add(endringUttak);

                    forrigeBeholdning = beholdning;
                }
            }
        }
        return forrigeBeholdning;
    }

    private void addDetailsToReguleringEndring(EndringPensjonsopptjening endring, Integer givenYear) {
        if (givenYear == REFORM_2010) {
            endring.setArsakDetails(List.of(DetailsArsakCode.REGULERING_2010));
        } else if (givenYear > REFORM_2010) {
            endring.setArsakDetails(List.of(DetailsArsakCode.REGULERING));
        }
    }

    /**
     * Adding the changes on beholdning due to changes on uttaksgrad, between may the 1st and 31st of december, to the
     * PensjonsbeholdningEndring list, if uttaksgradchanges exists.
     *
     * @param beholdninger         the sorted beholdninger
     * @param beholdningsendringer the PensjonsbeholdningEndring
     * @param forrigeBeholdning    the previous beholdning
     */
    private void addChangesUttaksgradAfterReguleringAtMay1th(List<Beholdning> beholdninger,
                                                             List<EndringPensjonsopptjening> beholdningsendringer, Beholdning forrigeBeholdning, List<Uttaksgrad> uttaksgrader) {
        Beholdning prevBeholdning = forrigeBeholdning;

        for (Beholdning beholdning : beholdninger) {
            if (beholdning.getFomDato().isEqual(reguleringDayGivenYear)) {
                prevBeholdning = beholdning;
            } else if (isBeholdningWithinPeriode(beholdning, reguleringDayGivenYear, lastDayOfGivenYear)) {
                Double prevBelop = prevBeholdning != null ? prevBeholdning.getBelop() : 0D;
                Double endringBelop = beholdning.getBelop() - prevBelop;

                EndringPensjonsopptjening endringUttak = new EndringPensjonsopptjening(TypeArsakCode.UTTAK, beholdning.getFomDato(), endringBelop, beholdning.getBelop(),
                        fetchUttaksgrad(beholdning.getFomDato(), beholdning, uttaksgrader), null);
                endringUttak.setArsakDetails(List.of(DetailsArsakCode.UTTAK));
                beholdningsendringer.add(endringUttak);

                prevBeholdning = beholdning;
            }
        }
    }

    private void addUtgaendeBeholdning(List<Beholdning> sortedBeholdningListe, List<EndringPensjonsopptjening> beholdningsendringer, List<Uttaksgrad> sakLuttaksgradList) {
        Beholdning lastBeholdning = sortedBeholdningListe.get(sortedBeholdningListe.size() - 1);
        if (lastBeholdning.getTomDato() != null && lastBeholdning.getTomDato().isEqual(lastDayOfGivenYear)) {
            beholdningsendringer.add(new EndringPensjonsopptjening(TypeArsakCode.UTGAENDE, lastDayOfGivenYear, null, lastBeholdning.getBelop(), fetchUttaksgrad(
                    lastDayOfGivenYear, lastBeholdning, sakLuttaksgradList), null));
        }
    }
}
