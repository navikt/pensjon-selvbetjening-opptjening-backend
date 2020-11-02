package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.isDateInPeriod;
import static no.nav.pensjon.selvbetjeningopptjening.util.PeriodeUtil.isPeriodeWithinInterval;
import static no.nav.pensjon.selvbetjeningopptjening.util.PeriodeUtil.sortPerioderByFomDate;

public class EndringPensjonsbeholdningCalculator {

    private LocalDate lastDayOfPreviousYear;
    private LocalDate firstDayOfGivenYear;
    private LocalDate dayBeforeReguleringGivenYear;
    private LocalDate reguleringDayGivenYear;
    private LocalDate lastDayOfGivenYear;

    List<EndringPensjonsopptjening> calculatePensjonsbeholdningsendringer(int year,
                                                                          List<Beholdning> beholdninger,
                                                                          List<Uttaksgrad> uttaksgrader) {
        List<Beholdning> sortedBeholdninger = sortPerioderByFomDate(beholdninger);

        firstDayOfGivenYear = LocalDate.of(year, Month.JANUARY, 1);
        dayBeforeReguleringGivenYear = LocalDate.of(year, Month.APRIL, 30);
        reguleringDayGivenYear = LocalDate.of(year, Month.MAY, 1);
        lastDayOfGivenYear = LocalDate.of(year, Month.DECEMBER, 31);
        lastDayOfPreviousYear = LocalDate.of(year - 1, Month.DECEMBER, 31);

        return sortedBeholdninger.isEmpty()
                ? null
                : getPensjonsbeholdningsendringer(year, sortedBeholdninger, uttaksgrader);
    }

    private List<EndringPensjonsopptjening> getPensjonsbeholdningsendringer(int year,
                                                                            List<Beholdning> sortedBeholdninger,
                                                                            List<Uttaksgrad> uttaksgrader) {
        List<EndringPensjonsopptjening> endringer = new ArrayList<>();
        Beholdning previousBeholdning = addInngaendeBeholdning(year, sortedBeholdninger, endringer, uttaksgrader);
        previousBeholdning = addNyOpptjening(year, sortedBeholdninger, previousBeholdning, endringer, uttaksgrader);
        previousBeholdning = addChangesUttaksgradBeforeRegulering(sortedBeholdninger, previousBeholdning, endringer, uttaksgrader);
        previousBeholdning = addRegulering(year, sortedBeholdninger, previousBeholdning, endringer, uttaksgrader);
        addChangesUttaksgradAfterRegulering(sortedBeholdninger, previousBeholdning, endringer, uttaksgrader);
        addUtgaendeBeholdning(year, sortedBeholdninger, endringer, uttaksgrader);
        return endringer;
    }

    private Beholdning addInngaendeBeholdning(int year,
                                              List<Beholdning> beholdninger,
                                              List<EndringPensjonsopptjening> beholdningsendringer,
                                              List<Uttaksgrad> uttaksgrader) {
        Beholdning beholdning = beholdningAtEndOfPreviousYear(beholdninger);

        beholdningsendringer.add(
                EndringPensjonsopptjening.inngaende(
                        year,
                        beholdning.getBelop(),
                        uttaksgradAtEndOfPreviousYear(uttaksgrader, beholdning)));

        return beholdning;
    }

    private Beholdning addNyOpptjening(int year,
                                       List<Beholdning> beholdninger,
                                       Beholdning previousBeholdning,
                                       List<EndringPensjonsopptjening> beholdningsendringer,
                                       List<Uttaksgrad> uttaksgrader) {
        Beholdning beholdning = beholdningAtStartOfYear(beholdninger);

        if (beholdning == null) {
            return previousBeholdning;
        }

        Double inngaendeBelop = beholdningsendringer.get(0).getBeholdningsbelop();
        double innskudd = beholdning.getEffectiveInnskudd(year);
        double beholdningsbelop = inngaendeBelop + innskudd;

        if (isBeholdningWithinUttaksgradsperiodeIncludeSameDay(beholdning, uttaksgrader)) {
            addNyOpptjening(year, beholdning, beholdningsendringer, uttaksgrader, innskudd, beholdningsbelop);
            addUttakAtStartOfYear(year, beholdning, beholdningsendringer, uttaksgrader, beholdningsbelop);
        } else {
            addNyOpptjening(year, beholdning, beholdningsendringer, uttaksgrader, innskudd, beholdningsbelop);
        }

        return beholdning;
    }

    /**
     * Adds changes to beholdning due to changes in uttaksgrad, between 1 January and 31 April,
     * to the PensjonsbeholdningEndring list, if uttaksgrad changes exist.
     */
    private Beholdning addChangesUttaksgradBeforeRegulering(List<Beholdning> beholdninger,
                                                            Beholdning previousBeholdning,
                                                            List<EndringPensjonsopptjening> beholdningsendringer,
                                                            List<Uttaksgrad> uttaksgrader) {
        double previousBeholdningsbelop = 0D;

        for (Beholdning beholdning : beholdninger) {
            if (startsFirstDayOfGivenYear(beholdning)) {
                previousBeholdningsbelop = beholdning.getBelop();
                previousBeholdning = beholdning;
            } else if (isPeriodeWithinInterval(beholdning, firstDayOfGivenYear, reguleringDayGivenYear)) {
                double endringsbelop = beholdning.getBelop() - previousBeholdningsbelop;

                beholdningsendringer.add(
                        EndringPensjonsopptjening.uttak(
                                beholdning.getFomDato(),
                                beholdning.getBelop(),
                                endringsbelop,
                                uttaksgradAtFomDate(beholdning, uttaksgrader)));

                previousBeholdningsbelop = beholdning.getBelop();
                previousBeholdning = beholdning;
            }
        }

        return previousBeholdning;
    }

    private Beholdning addRegulering(int year,
                                     List<Beholdning> beholdninger,
                                     Beholdning previousBeholdning,
                                     List<EndringPensjonsopptjening> beholdningsendringer,
                                     List<Uttaksgrad> uttaksgrader) {
        Double lastBeholdningsbelop = getLastElement(beholdningsendringer).getBeholdningsbelop();

        for (Beholdning beholdning : beholdninger) {
            previousBeholdning = addRegulering(
                    year,
                    beholdning,
                    previousBeholdning,
                    beholdningsendringer,
                    uttaksgrader,
                    lastBeholdningsbelop);
        }

        return previousBeholdning;
    }

    /**
     * Adds changes in beholdning due to changes in uttaksgrad, between 1 May and 31 December,
     * to the PensjonsbeholdningEndring list, if uttaksgrad changes exist.
     */
    private void addChangesUttaksgradAfterRegulering(List<Beholdning> sortedBeholdninger,
                                                     Beholdning previousBeholdning,
                                                     List<EndringPensjonsopptjening> beholdningsendringer,
                                                     List<Uttaksgrad> uttaksgrader) {
        Beholdning prevBeholdning = previousBeholdning;

        for (Beholdning beholdning : sortedBeholdninger) {
            prevBeholdning = addUttaksopptjening(beholdning, prevBeholdning, beholdningsendringer, uttaksgrader);
        }
    }

    private void addUtgaendeBeholdning(int year,
                                       List<Beholdning> sortedBeholdninger,
                                       List<EndringPensjonsopptjening> beholdningsendringer,
                                       List<Uttaksgrad> uttaksgrader) {
        Beholdning lastBeholdning = getLastElement(sortedBeholdninger);

        if (!endsLastDayOfGivenYear(lastBeholdning)) {
            return;
        }

        beholdningsendringer.add(
                EndringPensjonsopptjening.utgaende(
                        year,
                        lastBeholdning.getBelop(),
                        uttaksgradAtEndOfYear(uttaksgrader, lastBeholdning)));
    }

    private void addNyOpptjening(int year,
                                 Beholdning beholdning,
                                 List<EndringPensjonsopptjening> endringer,
                                 List<Uttaksgrad> uttaksgrader,
                                 double innskudd,
                                 double beholdningsbelop) {
        endringer.add(
                EndringPensjonsopptjening.nyOpptjening(
                        year,
                        beholdningsbelop,
                        innskudd,
                        uttaksgradAtStartOfYear(uttaksgrader, beholdning),
                        beholdning.getGrunnlag(),
                        beholdning.getOpptjeningGrunnlagTypes()));
    }

    private void addUttakAtStartOfYear(int year,
                                       Beholdning beholdning,
                                       List<EndringPensjonsopptjening> endringer,
                                       List<Uttaksgrad> uttaksgrader,
                                       double beholdningsbelop) {
        double endringsbelop = beholdning.getBelop() - beholdningsbelop;

        endringer.add(
                EndringPensjonsopptjening.uttakAtStartOfYear(
                        year,
                        beholdning.getBelop(),
                        endringsbelop,
                        uttaksgradAtStartOfYear(uttaksgrader, beholdning)));
    }

    private Beholdning addRegulering(int year,
                                     Beholdning beholdning,
                                     Beholdning previousBeholdning,
                                     List<EndringPensjonsopptjening> beholdningsendringer,
                                     List<Uttaksgrad> uttaksgrader,
                                     Double lastBeholdningsbelop) {
        if (!beholdning.getFomDato().isEqual(reguleringDayGivenYear)) {
            return previousBeholdning;
        }

        double vedtakPensjonseringsbelop = 0D;

        if (beholdning.hasLonnsvekstreguleringsbelop()) {
            double lonnsvekstreguleringsbelop = beholdning.getLonnsvekstreguleringsbelop();
            vedtakPensjonseringsbelop = lonnsvekstreguleringsbelop + lastBeholdningsbelop;

            beholdningsendringer.add(
                    EndringPensjonsopptjening.regulering(
                            year,
                            vedtakPensjonseringsbelop,
                            lonnsvekstreguleringsbelop,
                            uttaksgradOnReguleringDate(uttaksgrader, beholdning, vedtakPensjonseringsbelop)));

            previousBeholdning = beholdning;
        }

        if (isBeholdningWithinUttaksgradsperiodeIncludeSameDay(beholdning, uttaksgrader)) {
            double endringsbelop = beholdning.getBelop() - vedtakPensjonseringsbelop;

            beholdningsendringer.add(
                    EndringPensjonsopptjening.uttakOnReguleringDate(
                            year,
                            beholdning.getBelop(),
                            endringsbelop,
                            uttaksgradForUttakOnReguleringDate(uttaksgrader, beholdning)));

            previousBeholdning = beholdning;
        }

        return previousBeholdning;
    }

    private Beholdning addUttaksopptjening(Beholdning beholdning,
                                           Beholdning previousBeholdning,
                                           List<EndringPensjonsopptjening> beholdningsendringer,
                                           List<Uttaksgrad> uttaksgrader) {
        LocalDate fomDate = beholdning.getFomDato();

        if (fomDate.isEqual(reguleringDayGivenYear)) {
            return beholdning;
        }

        if (!isPeriodeWithinInterval(beholdning, reguleringDayGivenYear, lastDayOfGivenYear)) {
            return previousBeholdning;
        }

        double previousBelop = previousBeholdning == null ? 0D : previousBeholdning.getBelop();
        double belop = beholdning.getBelop();
        double endringsbelop = belop - previousBelop;

        beholdningsendringer.add(
                EndringPensjonsopptjening.uttak(
                        fomDate,
                        belop,
                        endringsbelop,
                        uttaksgradAtFomDate(beholdning, uttaksgrader)));

        return beholdning;
    }

    private int uttaksgradAtEndOfPreviousYear(List<Uttaksgrad> uttaksgrader, Beholdning beholdning) {
        return uttaksgradAtDate(uttaksgrader, beholdning, lastDayOfPreviousYear);
    }

    private Integer uttaksgradAtStartOfYear(List<Uttaksgrad> uttaksgrader, Beholdning beholdning) {
        return uttaksgradAtDate(uttaksgrader, beholdning, firstDayOfGivenYear);
    }

    private Integer uttaksgradAtEndOfYear(List<Uttaksgrad> uttaksgrader, Beholdning beholdning) {
        return uttaksgradAtDate(uttaksgrader, beholdning, lastDayOfGivenYear);
    }

    private Integer uttaksgradForUttakOnReguleringDate(List<Uttaksgrad> uttaksgrader, Beholdning beholdning) {
        return uttaksgradAtDate(uttaksgrader, beholdning, reguleringDayGivenYear);
    }

    private Integer uttaksgradOnReguleringDate(List<Uttaksgrad> uttaksgrader,
                                               Beholdning beholdning,
                                               double vedtakPensjonseringsbelop) {
        LocalDate validityDate = beholdning.getBelop() == vedtakPensjonseringsbelop
                ? reguleringDayGivenYear
                : dayBeforeReguleringGivenYear;

        return uttaksgradAtDate(uttaksgrader, beholdning, validityDate);
    }

    private Beholdning beholdningAtEndOfPreviousYear(List<Beholdning> beholdninger) {
        return beholdninger
                .stream()
                .filter(this::endsLastDayOfPreviousYear)
                .findFirst()
                .orElse(Beholdning.NULL);
    }

    private Beholdning beholdningAtStartOfYear(List<Beholdning> beholdninger) {
        return beholdninger
                .stream()
                .filter(this::startsFirstDayOfGivenYear)
                .findFirst()
                .orElse(null);
    }

    private boolean startsFirstDayOfGivenYear(Beholdning beholdning) {
        return beholdning.getFomDato().isEqual(firstDayOfGivenYear);
    }

    private boolean endsLastDayOfGivenYear(Beholdning beholdning) {
        LocalDate tomDato = beholdning.getTomDato();
        return tomDato != null && tomDato.isEqual(lastDayOfGivenYear);
    }

    private boolean endsLastDayOfPreviousYear(Beholdning beholdning) {
        LocalDate tomDato = beholdning.getTomDato();
        return tomDato != null && tomDato.isEqual(lastDayOfPreviousYear);
    }

    private static Integer findUttaksgrad(LocalDate date, long vedtakId, List<Uttaksgrad> uttaksgrader) {
        return uttaksgrader.stream()
                .filter(uttaksgrad -> matchesVedtakAndDate(uttaksgrad, vedtakId, date))
                .findFirst()
                .map(Uttaksgrad::getUttaksgrad)
                .orElse(0);
    }

    private static Integer uttaksgradAtFomDate(Beholdning beholdning, List<Uttaksgrad> uttaksgrader) {
        return uttaksgradAtDate(uttaksgrader, beholdning, beholdning.getFomDato());
    }

    private static int uttaksgradAtDate(List<Uttaksgrad> uttaksgrader, Beholdning beholdning, LocalDate date) {
        return beholdning.hasVedtak()
                ? findUttaksgrad(date, beholdning.getVedtakId(), uttaksgrader)
                : 0;
    }

    private static boolean matchesVedtakAndDate(Uttaksgrad uttaksgrad, long vedtakId, LocalDate date) {
        return uttaksgrad.getVedtakId().equals(vedtakId)
                && isDateInPeriod(date, uttaksgrad.getFomDato(), uttaksgrad.getTomDato());
    }

    private static boolean isBeholdningWithinUttaksgradsperiodeIncludeSameDay(Beholdning beholdning,
                                                                              List<Uttaksgrad> uttaksgrader) {
        return uttaksgrader
                .stream()
                .anyMatch(uttaksgrad -> isBeholdningWithinUttaksgradsperiodeIncludeSameDay(beholdning, uttaksgrad));
    }

    private static boolean isBeholdningWithinUttaksgradsperiodeIncludeSameDay(Beholdning beholdning,
                                                                              Uttaksgrad uttaksgrad) {
        LocalDate beholdningFom = beholdning.getFomDato();
        LocalDate beholdningTom = beholdning.getTomDato();
        LocalDate uttaksgradFom = uttaksgrad.getFomDato();
        LocalDate uttaksgradTom = uttaksgrad.getTomDato();

        return beholdningFom.compareTo(uttaksgradFom) > -1
                && (beholdningTom == null && (uttaksgradTom == null || uttaksgradTom.compareTo(beholdningFom) > -1)
                || (beholdningTom != null && (uttaksgradTom == null || beholdningTom.compareTo(uttaksgradTom) < 1)));
    }

    private static <T> T getLastElement(List<T> list) {
        return list.get(list.size() - 1);
    }
}
