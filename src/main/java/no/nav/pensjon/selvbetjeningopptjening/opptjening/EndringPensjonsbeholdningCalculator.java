package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.*;
import static no.nav.pensjon.selvbetjeningopptjening.util.PeriodeUtil.isPeriodeWithinInterval;
import static no.nav.pensjon.selvbetjeningopptjening.util.PeriodeUtil.sortPerioderByFomDate;

public class EndringPensjonsbeholdningCalculator {

    static List<EndringPensjonsopptjening> calculatePensjonsbeholdningsendringer(int year,
                                                                                 List<Beholdning> beholdninger,
                                                                                 List<Uttaksgrad> uttaksgrader) {
        List<Beholdning> sortedBeholdninger = sortPerioderByFomDate(beholdninger);

        return sortedBeholdninger.isEmpty()
                ? emptyList()
                : getPensjonsbeholdningsendringer(year, sortedBeholdninger, uttaksgrader);
    }

    private static List<EndringPensjonsopptjening> getPensjonsbeholdningsendringer(int year,
                                                                                   List<Beholdning> sortedBeholdninger,
                                                                                   List<Uttaksgrad> uttaksgrader) {
        List<EndringPensjonsopptjening> endringer = new ArrayList<>();
        Beholdning previousBeholdning = addInngaendeBeholdning(year, sortedBeholdninger, endringer, uttaksgrader);
        previousBeholdning = addNyOpptjening(year, sortedBeholdninger, previousBeholdning, endringer, uttaksgrader);
        previousBeholdning = addChangesUttaksgradBeforeRegulering(year, sortedBeholdninger, previousBeholdning, endringer, uttaksgrader);
        previousBeholdning = addRegulering(year, sortedBeholdninger, previousBeholdning, endringer, uttaksgrader);
        addChangesUttaksgradAfterRegulering(year, sortedBeholdninger, previousBeholdning, endringer, uttaksgrader);
        addUtgaendeBeholdning(year, sortedBeholdninger, endringer, uttaksgrader);
        return endringer;
    }

    private static Beholdning addInngaendeBeholdning(int year,
                                                     List<Beholdning> beholdninger,
                                                     List<EndringPensjonsopptjening> beholdningsendringer,
                                                     List<Uttaksgrad> uttaksgrader) {
        Beholdning beholdningAtEndOfLastYear = beholdningAtEndOfYear(year - 1, beholdninger);

        beholdningsendringer.add(
                EndringPensjonsopptjening.inngaende(
                        year,
                        beholdningAtEndOfLastYear.getBelop(),
                        uttaksgradAtEndOfYear(year - 1, uttaksgrader, beholdningAtEndOfLastYear)));

        return beholdningAtEndOfLastYear;
    }

    private static Beholdning addNyOpptjening(int year,
                                              List<Beholdning> beholdninger,
                                              Beholdning previousBeholdning,
                                              List<EndringPensjonsopptjening> beholdningsendringer,
                                              List<Uttaksgrad> uttaksgrader) {
        Beholdning beholdning = beholdningAtStartOfYear(year, beholdninger);

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
    private static Beholdning addChangesUttaksgradBeforeRegulering(int year,
                                                                   List<Beholdning> beholdninger,
                                                                   Beholdning previousBeholdning,
                                                                   List<EndringPensjonsopptjening> beholdningsendringer,
                                                                   List<Uttaksgrad> uttaksgrader) {
        double previousBeholdningsbelop = 0D;

        for (Beholdning beholdning : beholdninger) {
            if (beholdning.startsFirstDayOf(year)) {
                previousBeholdningsbelop = beholdning.getBelop();
                previousBeholdning = beholdning;
            } else if (isPeriodeWithinInterval(beholdning, firstDayOf(year), reguleringDayOf(year))) {
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

    private static Beholdning addRegulering(int year,
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
    private static void addChangesUttaksgradAfterRegulering(int year,
                                                            List<Beholdning> sortedBeholdninger,
                                                            Beholdning previousBeholdning,
                                                            List<EndringPensjonsopptjening> beholdningsendringer,
                                                            List<Uttaksgrad> uttaksgrader) {
        Beholdning prevBeholdning = previousBeholdning;

        for (Beholdning beholdning : sortedBeholdninger) {
            prevBeholdning = addUttaksopptjening(year, beholdning, prevBeholdning, beholdningsendringer, uttaksgrader);
        }
    }

    private static void addUtgaendeBeholdning(int year,
                                              List<Beholdning> sortedBeholdninger,
                                              List<EndringPensjonsopptjening> beholdningsendringer,
                                              List<Uttaksgrad> uttaksgrader) {
        Beholdning lastBeholdning = getLastElement(sortedBeholdninger);

        if (!lastBeholdning.endsLastDayOf(year)) {
            return;
        }

        beholdningsendringer.add(
                EndringPensjonsopptjening.utgaende(
                        year,
                        lastBeholdning.getBelop(),
                        uttaksgradAtEndOfYear(year, uttaksgrader, lastBeholdning)));
    }

    private static void addNyOpptjening(int year,
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
                        uttaksgradAtStartOfYear(year, uttaksgrader, beholdning),
                        beholdning.getGrunnlag(),
                        beholdning.getOpptjeningGrunnlagTypes()));
    }

    private static void addUttakAtStartOfYear(int year,
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
                        uttaksgradAtStartOfYear(year, uttaksgrader, beholdning)));
    }

    private static Beholdning addRegulering(int year,
                                            Beholdning beholdning,
                                            Beholdning previousBeholdning,
                                            List<EndringPensjonsopptjening> beholdningsendringer,
                                            List<Uttaksgrad> uttaksgrader,
                                            Double lastBeholdningsbelop) {
        if (!beholdning.getFomDato().isEqual(reguleringDayOf(year))) {
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
                            uttaksgradOnReguleringDate(year, uttaksgrader, beholdning, vedtakPensjonseringsbelop)));

            previousBeholdning = beholdning;
        }

        if (isBeholdningWithinUttaksgradsperiodeIncludeSameDay(beholdning, uttaksgrader)) {
            double endringsbelop = beholdning.getBelop() - vedtakPensjonseringsbelop;

            beholdningsendringer.add(
                    EndringPensjonsopptjening.uttakOnReguleringDate(
                            year,
                            beholdning.getBelop(),
                            endringsbelop,
                            uttaksgradForUttakOnReguleringDate(year, uttaksgrader, beholdning)));

            previousBeholdning = beholdning;
        }

        return previousBeholdning;
    }

    private static Beholdning addUttaksopptjening(int year,
                                                  Beholdning beholdning,
                                                  Beholdning previousBeholdning,
                                                  List<EndringPensjonsopptjening> beholdningsendringer,
                                                  List<Uttaksgrad> uttaksgrader) {
        LocalDate fomDate = beholdning.getFomDato();
        LocalDate reguleringDate = reguleringDayOf(year);

        if (fomDate.isEqual(reguleringDate)) {
            return beholdning;
        }

        if (!isPeriodeWithinInterval(beholdning, reguleringDate, lastDayOf(year))) {
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

    private static Integer uttaksgradAtStartOfYear(int year, List<Uttaksgrad> uttaksgrader, Beholdning beholdning) {
        return uttaksgradAtDate(uttaksgrader, beholdning, firstDayOf(year));
    }

    private static Integer uttaksgradAtEndOfYear(int year, List<Uttaksgrad> uttaksgrader, Beholdning beholdning) {
        return uttaksgradAtDate(uttaksgrader, beholdning, lastDayOf(year));
    }

    private static Integer uttaksgradForUttakOnReguleringDate(int year, List<Uttaksgrad> uttaksgrader, Beholdning beholdning) {
        return uttaksgradAtDate(uttaksgrader, beholdning, reguleringDayOf(year));
    }

    private static Integer uttaksgradOnReguleringDate(int year,
                                                      List<Uttaksgrad> uttaksgrader,
                                                      Beholdning beholdning,
                                                      double vedtakPensjonseringsbelop) {
        LocalDate validityDate = beholdning.getBelop() == vedtakPensjonseringsbelop
                ? reguleringDayOf(year)
                : reguleringDayOf(year).minusDays(1);

        return uttaksgradAtDate(uttaksgrader, beholdning, validityDate);
    }

    private static Beholdning beholdningAtStartOfYear(int year, List<Beholdning> beholdninger) {
        return beholdninger
                .stream()
                .filter(beholdning -> beholdning.startsFirstDayOf(year))
                .findFirst()
                .orElse(null);
    }

    private static Beholdning beholdningAtEndOfYear(int year, List<Beholdning> beholdninger) {
        return beholdninger
                .stream()
                .filter(beholdning -> beholdning.endsLastDayOf(year))
                .findFirst()
                .orElse(Beholdning.NULL);
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
                .anyMatch(grad -> beholdning.isWithinInclusive(grad.getFomDato(), grad.getTomDato()));
    }

    private static <T> T getLastElement(List<T> list) {
        return list.get(list.size() - 1);
    }
}
