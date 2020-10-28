package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.BeholdningDto;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.opptjening.BeholdningMapper.fromDto;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.EndringPensjonsopptjeningMapper.toDto;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.isDateInPeriod;
import static no.nav.pensjon.selvbetjeningopptjening.util.PeriodeUtil.isPeriodeWithinInterval;
import static no.nav.pensjon.selvbetjeningopptjening.util.PeriodeUtil.sortPerioderByFomDate;

public class EndringPensjonsbeholdningCalculator {

    private LocalDate lastDayOfPreviousYear;
    private LocalDate firstDayOfGivenYear;
    private LocalDate dayBeforeReguleringGivenYear;
    private LocalDate reguleringDayGivenYear;
    private LocalDate lastDayOfGivenYear;

    List<EndringPensjonsopptjeningDto> calculateEndringPensjonsbeholdning(int year, List<BeholdningDto> beholdninger, List<Uttaksgrad> uttaksgrader) {
        List<Beholdning> sortedBeholdninger = sortPerioderByFomDate(fromDto(beholdninger));

        firstDayOfGivenYear = LocalDate.of(year, Month.JANUARY, 1);
        dayBeforeReguleringGivenYear = LocalDate.of(year, Month.APRIL, 30);
        reguleringDayGivenYear = LocalDate.of(year, Month.MAY, 1);
        lastDayOfGivenYear = LocalDate.of(year, Month.DECEMBER, 31);
        lastDayOfPreviousYear = LocalDate.of(year - 1, Month.DECEMBER, 31);

        return sortedBeholdninger.isEmpty()
                ? null
                : toDto(createPensjonsbeholdningEndringListe(sortedBeholdninger, year, uttaksgrader));
    }

    private List<EndringPensjonsopptjening> createPensjonsbeholdningEndringListe(List<Beholdning> sortedBeholdninger, int year, List<Uttaksgrad> uttaksgrader) {
        List<EndringPensjonsopptjening> beholdningsendringer = new ArrayList<>();

        Beholdning forrigeBeholdning = addInngaendeBeholdning(sortedBeholdninger, beholdningsendringer, uttaksgrader, year);
        forrigeBeholdning = addNyOpptjening(sortedBeholdninger, beholdningsendringer, forrigeBeholdning, year, uttaksgrader);
        forrigeBeholdning = addChangesUttaksgradBeforeRegulering(sortedBeholdninger, beholdningsendringer, forrigeBeholdning, uttaksgrader);
        forrigeBeholdning = addRegulering(sortedBeholdninger, beholdningsendringer, forrigeBeholdning, uttaksgrader, year);
        addChangesUttaksgradAfterRegulering(sortedBeholdninger, beholdningsendringer, forrigeBeholdning, uttaksgrader);
        addUtgaendeBeholdning(sortedBeholdninger, beholdningsendringer, uttaksgrader, year);

        return beholdningsendringer;
    }

    private Beholdning addInngaendeBeholdning(List<Beholdning> beholdninger, List<EndringPensjonsopptjening> beholdningsendringer, List<Uttaksgrad> uttaksgrader, int year) {
        double pensjonsbeholdningBelop = 0D;
        Beholdning beholdningUsed = null;
        Integer uttaksgrad = 0;

        for (Beholdning beholdning : beholdninger) {
            if (beholdning.getTomDato() != null && beholdning.getTomDato().isEqual(lastDayOfPreviousYear)) {
                pensjonsbeholdningBelop = beholdning.getBelop();
                beholdningUsed = beholdning;
                uttaksgrad = findUttaksgrad(lastDayOfPreviousYear, beholdningUsed, uttaksgrader);
            }
        }

        beholdningsendringer.add(EndringPensjonsopptjening.inngaende(year, pensjonsbeholdningBelop, uttaksgrad));
        return beholdningUsed;
    }

    private static Integer findUttaksgrad(LocalDate date, Beholdning beholdning, List<Uttaksgrad> uttaksgrader) {
        if (beholdning == null || !beholdning.hasVedtak()) {
            return 0;
        }

        return uttaksgrader.stream()
                .filter(uttaksgrad -> uttaksgrad.getVedtakId().equals(beholdning.getVedtakId())
                        && isDateInPeriod(date, uttaksgrad.getFomDato(), uttaksgrad.getTomDato()))
                .findFirst()
                .map(Uttaksgrad::getUttaksgrad)
                .orElse(0);
    }

    private Beholdning addNyOpptjening(List<Beholdning> beholdninger, List<EndringPensjonsopptjening> beholdningsendringer,
                                       Beholdning forrigeBeholdning, int year, List<Uttaksgrad> uttaksgrader) {
        Double inngaendeBelop = beholdningsendringer.get(0).getBeholdningsbelop();
        Beholdning beholdning = findBeholdningAtStartOfYear(beholdninger);

        if (beholdning == null) {
            return forrigeBeholdning;
        }

        double innskudd = calculateInnskudd(year, beholdning);
        double pensjonsbeholdningBelop = inngaendeBelop + innskudd;

        if (isBeholdningWithinUttaksgradPeriodeIncludeSameDay(beholdning, uttaksgrader)) {
            addNyOpptjeningWhenBeholdningWithinUttaksgradPeriode(beholdningsendringer, year, uttaksgrader, beholdning, innskudd, pensjonsbeholdningBelop);
        } else {
            addNyOpptjeningWhenBeholdningOutsideUttaksgradPeriode(beholdningsendringer, year, uttaksgrader, beholdning, innskudd, pensjonsbeholdningBelop);
        }

        forrigeBeholdning = beholdning;
        return forrigeBeholdning;
    }

    private void addNyOpptjeningWhenBeholdningOutsideUttaksgradPeriode(List<EndringPensjonsopptjening> beholdningsendringer,
                                                                       int year,
                                                                       List<Uttaksgrad> uttaksgrader,
                                                                       Beholdning beholdning,
                                                                       double innskudd,
                                                                       double pensjonsbeholdningBelop) {
        Integer uttaksgrad = findUttaksgrad(firstDayOfGivenYear, beholdning, uttaksgrader);

        beholdningsendringer.add(
                EndringPensjonsopptjening.nyOpptjening(
                        year,
                        pensjonsbeholdningBelop,
                        innskudd,
                        uttaksgrad,
                        beholdning.getGrunnlag(),
                        beholdning.getOpptjeningGrunnlagTypes()));
    }

    private void addNyOpptjeningWhenBeholdningWithinUttaksgradPeriode(List<EndringPensjonsopptjening> beholdningsendringer,
                                                                      int year,
                                                                      List<Uttaksgrad> uttaksgrader,
                                                                      Beholdning beholdning,
                                                                      double innskudd,
                                                                      double beholdningsbelop) {
        addNyOpptjeningWhenBeholdningOutsideUttaksgradPeriode(beholdningsendringer, year, uttaksgrader, beholdning, innskudd, beholdningsbelop);
        Integer uttaksgrad = findUttaksgrad(firstDayOfGivenYear, beholdning, uttaksgrader);
        double endringsbelop = beholdning.getBelop() - beholdningsbelop;

        beholdningsendringer.add(
                EndringPensjonsopptjening.uttakAtStartOfYear(year, beholdning.getBelop(), endringsbelop, uttaksgrad));
    }

    /**
     * Det kan hende at ny opptjening ennå ikke har blitt oppdatert, men det eksisterer likevel en beholdning 01.01 hvis
     * har endret uttaksgrad.
     */
    private double calculateInnskudd(int year, Beholdning beholdning) {
        if (!beholdning.hasInnskudd()) {
            return 0D;
        }

        double innskudd = beholdning.getInnskudd();

        if (year == REFORM_2010) {
            innskudd += beholdning.getLonnsvekstreguleringsbelop();
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
                                                            List<EndringPensjonsopptjening> beholdningsendringer,
                                                            Beholdning forrigeBeholdning,
                                                            List<Uttaksgrad> uttaksgrader) {
        double previousBeholdningBelop = 0D;

        for (Beholdning beholdning : beholdninger) {
            if (beholdning.getFomDato().isEqual(firstDayOfGivenYear)) {
                previousBeholdningBelop = beholdning.getBelop();
                forrigeBeholdning = beholdning;
            } else if (isPeriodeWithinInterval(beholdning, firstDayOfGivenYear, reguleringDayGivenYear)) {
                Integer uttaksgrad = findUttaksgrad(beholdning.getFomDato(), beholdning, uttaksgrader);
                double endringsbelop = beholdning.getBelop() - previousBeholdningBelop;

                beholdningsendringer.add(
                        EndringPensjonsopptjening.uttak(
                                beholdning.getFomDato(),
                                beholdning.getBelop(),
                                endringsbelop,
                                uttaksgrad));

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
                                     Beholdning forrigeBeholdning, List<Uttaksgrad> uttaksgrader, int year) {
        Double tempBelop = beholdningsendringer.get(beholdningsendringer.size() - 1).getBeholdningsbelop();

        for (Beholdning beholdning : beholdninger) {
            if (beholdning.getFomDato().isEqual(reguleringDayGivenYear)) {
                LocalDate gyldigPaDato;
                double vedtakPensjonseringsbelop = 0D;

                if (beholdning.hasLonnsvekstreguleringsbelop()) {
                    double lonnsvekstreguleringsbelop = beholdning.getLonnsvekstreguleringsbelop();
                    vedtakPensjonseringsbelop = lonnsvekstreguleringsbelop + tempBelop;

                    gyldigPaDato = beholdning.getBelop() == vedtakPensjonseringsbelop
                            ? reguleringDayGivenYear
                            : dayBeforeReguleringGivenYear;

                    Integer uttaksgrad = findUttaksgrad(gyldigPaDato, beholdning, uttaksgrader);

                    beholdningsendringer.add(
                            EndringPensjonsopptjening.regulering(
                                    year,
                                    vedtakPensjonseringsbelop,
                                    lonnsvekstreguleringsbelop,
                                    uttaksgrad));

                    forrigeBeholdning = beholdning;
                }

                if (isBeholdningWithinUttaksgradPeriodeIncludeSameDay(beholdning, uttaksgrader)) {
                    double endringsbelop = beholdning.getBelop() - vedtakPensjonseringsbelop;
                    Integer uttaksgrad = findUttaksgrad(reguleringDayGivenYear, beholdning, uttaksgrader);

                    beholdningsendringer.add(
                            EndringPensjonsopptjening.uttakAtReguleringDate(
                                    year,
                                    beholdning.getBelop(),
                                    endringsbelop,
                                    uttaksgrad));

                    forrigeBeholdning = beholdning;
                }
            }
        }
        return forrigeBeholdning;
    }

    /**
     * Adding the changes on beholdning due to changes on uttaksgrad, between may the 1st and 31st of december, to the
     * PensjonsbeholdningEndring list, if uttaksgradchanges exists.
     *
     * @param beholdninger         the sorted beholdninger
     * @param beholdningsendringer the PensjonsbeholdningEndring
     * @param forrigeBeholdning    the previous beholdning
     */
    private void addChangesUttaksgradAfterRegulering(List<Beholdning> beholdninger,
                                                     List<EndringPensjonsopptjening> beholdningsendringer,
                                                     Beholdning forrigeBeholdning,
                                                     List<Uttaksgrad> uttaksgrader) {
        Beholdning prevBeholdning = forrigeBeholdning;

        for (Beholdning beholdning : beholdninger) {
            prevBeholdning = addUttaksopptjening(beholdningsendringer, uttaksgrader, prevBeholdning, beholdning);
        }
    }

    private Beholdning addUttaksopptjening(List<EndringPensjonsopptjening> beholdningsendringer,
                                           List<Uttaksgrad> uttaksgrader,
                                           Beholdning previousBeholdning,
                                           Beholdning beholdning) {
        LocalDate fomDato = beholdning.getFomDato();

        if (fomDato.isEqual(reguleringDayGivenYear)) {
            return beholdning;
        }

        if (!isPeriodeWithinInterval(beholdning, reguleringDayGivenYear, lastDayOfGivenYear)) {
            return previousBeholdning;
        }

        double prevBelop = previousBeholdning == null ? 0D : previousBeholdning.getBelop();
        double belop = beholdning.getBelop();
        double endringsbelop = belop - prevBelop;
        Integer uttaksgrad = findUttaksgrad(fomDato, beholdning, uttaksgrader);

        beholdningsendringer.add(
                EndringPensjonsopptjening.uttak(
                        fomDato,
                        belop,
                        endringsbelop,
                        uttaksgrad));

        return beholdning;
    }

    private void addUtgaendeBeholdning(List<Beholdning> sortedBeholdninger,
                                       List<EndringPensjonsopptjening> beholdningsendringer,
                                       List<Uttaksgrad> uttaksgrader,
                                       int year) {
        Beholdning lastBeholdning = getLast(sortedBeholdninger);

        if (lastBeholdning.getTomDato() == null || !lastBeholdning.getTomDato().isEqual(lastDayOfGivenYear)) {
            return;
        }

        Integer uttaksgrad = findUttaksgrad(lastDayOfGivenYear, lastBeholdning, uttaksgrader);
        beholdningsendringer.add(EndringPensjonsopptjening.utgaende(year, lastBeholdning.getBelop(), uttaksgrad));
    }

    private static Beholdning getLast(List<Beholdning> beholdninger) {
        return beholdninger.get(beholdninger.size() - 1);
    }
}
